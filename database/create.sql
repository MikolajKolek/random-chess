-- Tabelę openings możemy bazować np. na https://github.com/lichess-org/chess-openings
CREATE TABLE "openings"
(
    "id"                SERIAL          PRIMARY KEY,
    -- ECO: https://en.wikipedia.org/wiki/Encyclopaedia_of_Chess_Openings
    "eco"               CHAR(3)         NOT NULL,
    "name"              VARCHAR(256)    NOT NULL,
    -- FEN: https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation
    -- Partial FEN - pierwsze 4 pola FEN, bez informacji o licznikach ruchów
    "partial_fen"               VARCHAR         UNIQUE NOT NULL
);


CREATE TABLE "users"
(
    "id"                SERIAL          PRIMARY KEY,
    "email"             VARCHAR         UNIQUE NOT NULL,
    "password_hash"     VARCHAR         NOT NULL,
    "elo"               NUMERIC         NOT NULL DEFAULT 1500,
    -- Regex pochodzi z https://emailregex.com/
    CHECK (email ~* '(?:[a-z0-9!#$%&''''*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&''''*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])')
);


CREATE TABLE "game_services"
(
    "id"   SERIAL       PRIMARY KEY,
    "name" VARCHAR(256) UNIQUE NOT NULL
);
INSERT INTO game_services(name) VALUES ('Random Chess');


-- Dla każdego użytkownika istnieje dokładnie jeden service_account z service_id naszego serwisu (1)
-- Przechowywana tam nazwa użytkownika jest jego nazwą w naszym serwisie
CREATE TABLE "service_accounts"
(
    "user_id"            INT          NULL REFERENCES users ("id") ON DELETE SET NULL,
    "service_id"         INT          NOT NULL REFERENCES game_services ("id"),
    "user_id_in_service" VARCHAR      NOT NULL,
    "display_name"       VARCHAR(256) NOT NULL,
    "is_bot"             BOOL         NOT NULL,
    PRIMARY KEY ("service_id", "user_id_in_service"),
    -- Dla service_accounts w naszym serwisie (z service_id = 1) zawsze zachodzi jedna z dwóch opcji:
    -- - Konto to bot, w jakim razie is_bot = TRUE, user_id IS NULL
    -- - Konto to użytkownik, w jakim razie albo użytkownik istnieje i user_id = user_id_in_service, albo
    --   użytkownik został już usunięty, i user_id IS NULL
    CHECK (
        ("service_id" != 1) OR
        ((is_bot = TRUE) AND (user_id IS NULL)) OR
        ((is_bot = FALSE) AND ((user_id::varchar = user_id_in_service) OR (user_id IS NULL)))
    )
);


-- Tworzymy dwie tabele reprezentujące rozegrane gry: service_games i pgn_games.
-- Niektóre z ich kolumn się pokrywają.
-- Ze względu na ograniczenia mechanizmów polimorficzności w PostgreSQL zdecydowaliśmy,
-- że jest to najlepsze rozwiązanie. Opis innych rozważanych rozwiązań dołączamy
-- do zgłoszenia projektu.
--
-- "id" w tabelach service_games i pgn_games są unikalne tylko w obrębie danej tabeli.

-- TODO: napisać funkcję generującą listę pozycji w formacie FEN dla gry
-- i dodać to wspólne pole do pgn_games i serial_games:
-- "partial_fen_positions"     VARCHAR[]       GENERATED ALWAYS AS ()

CREATE TABLE "service_games"
(
    "id"                 SERIAL         PRIMARY KEY,
    -- kolumny wspólne dla "service_games" i "pgn_games"
    "moves"              VARCHAR         NOT NULL,
    "date"               TIMESTAMP       NULL,
    "metadata"           JSONB           NULL,
    -- kolumny występujące tylko w "service_games"
    "game_id_in_service" VARCHAR        NULL,
    "service_id"         INT            NOT NULL    REFERENCES "game_services" ("id"),
    "white_player"       VARCHAR        NOT NULL,
    "black_player"       VARCHAR        NOT NULL,
    -- Partie rozegrane w naszym serwisie mają "game_id_in_service" ustawione na NULL,
    -- a w innych serwisach zawsze mają ustawioną wartość.
    CHECK (CASE
        WHEN "service_id" = 1 THEN "game_id_in_service" IS NULL
        ELSE "game_id_in_service" IS NOT NULL
    END),

    UNIQUE ("game_id_in_service", "service_id"),
    FOREIGN KEY ("service_id", "white_player")
        REFERENCES "service_accounts" ("service_id", "user_id_in_service"),
    FOREIGN KEY ("service_id", "black_player")
        REFERENCES "service_accounts" ("service_id", "user_id_in_service")
);

CREATE TABLE "pgn_games"
(
    "id"                SERIAL          PRIMARY KEY,
    -- kolumny wspólne dla "service_games" i "pgn_games"
    "moves"             VARCHAR         NOT NULL,
    "date"              TIMESTAMP       NULL,
    "metadata"          JSONB           NULL,
    -- kolumny występujące tylko w "pgn_games"
    "owner_id"          INT             NOT NULL    REFERENCES "users" ("id") ON DELETE CASCADE,
    "black_player_name" VARCHAR         NOT NULL,
    "white_player_name" VARCHAR         NOT NULL
);


-- Wartości "id" mogą się powtarzać, ale już pary ("id", "kind") są unikatowe
CREATE VIEW "games" AS (
    SELECT "id", 'service' AS "kind", "moves", "date", "metadata" FROM service_games
    UNION ALL
    SELECT "id", 'pgn' AS "kind", "moves", "date", "metadata" FROM pgn_games
);

CREATE VIEW "users_games" AS (
    SELECT sa."user_id" as "user_id", sg."id" as "game_id", 'service' AS "kind", "moves", "date", "metadata"
    FROM service_accounts sa
    JOIN service_games sg ON (sa.user_id_in_service = sg.white_player) OR (sa.user_id_in_service = sg.black_player)
    UNION
    SELECT pg."owner_id" AS "user_id", pg."id" AS "game_id", 'pgn' as "kind", "moves", "date", "metadata"
    FROM pgn_games pg
);

-- TODO: stworzyć view który na podstawie tabeli openings i epd_positions w games przypisuje każdej grze opening
/*CREATE VIEW games_openings AS (

);*/


-- Poniższe triggery sprawiają, że service_account użytkownika w naszym serwisie
-- zawsze istnieje póki użytkownik istnieje
CREATE OR REPLACE FUNCTION add_default_service_to_user()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    INSERT INTO service_accounts(user_id, service_id, user_id_in_service, display_name, is_bot) VALUES (
       NEW.id, 1, NEW.id, NEW.email, FALSE
    );
    RETURN NEW;
END;
$$;

CREATE OR REPLACE FUNCTION prevent_default_service_modification()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    -- Sprawdzenie pg_trigger_depth() = 1 sprawia, że ON DELETE SET NULL
    -- w "user_id" service_accounts może zadziałać
    IF (OLD.service_id = 1) AND (old.user_id IS NOT NULL) AND (pg_trigger_depth() = 1) AND
       (OLD.user_id != NEW.user_id OR OLD.service_id != NEW.service_id OR
        OLD.user_id_in_service != NEW.user_id_in_service OR OLD.is_bot != NEW.is_bot)
    THEN
        RAISE EXCEPTION 'Cannot modify default service account for user %', OLD.user_id;
    END IF;

    RETURN NEW;
END;
$$;

CREATE OR REPLACE FUNCTION prevent_default_service_deletion()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    IF (OLD.service_id = 1) AND (old.user_id IS NOT NULL) THEN
        RAISE EXCEPTION 'Cannot delete default service account for user %', OLD.user_id;
    END IF;

    RETURN OLD;
END;
$$;

CREATE OR REPLACE TRIGGER users_insert_add_default_service
    AFTER INSERT ON users
    FOR EACH ROW
EXECUTE FUNCTION add_default_service_to_user();

CREATE OR REPLACE TRIGGER service_accounts_update_prevent_for_default_service
    BEFORE UPDATE ON service_accounts
    FOR EACH ROW
EXECUTE FUNCTION prevent_default_service_modification();

CREATE OR REPLACE TRIGGER service_accounts_delete_prevent_for_default_service
    BEFORE DELETE ON service_accounts
    FOR EACH ROW
EXECUTE FUNCTION prevent_default_service_deletion();



-- Przykładowe dane:
INSERT INTO game_services(name) VALUES
    ('chess.com'),
    ('lichess.org');

INSERT INTO users(email, password_hash) VALUES
    ('test@[1.1.1.1]', '1234'),
    ('email.test@gmail.com', '0000'),
    ('test2.user@interia.pl', '1111');

UPDATE service_accounts SET display_name = 'test user' WHERE user_id = 1;

INSERT INTO service_accounts("user_id", "service_id", "user_id_in_service", "is_bot", "display_name") VALUES
    (2, 2, 'test_ccom_id', FALSE, 'test_chesscom'),
    (3, 2, 'test2_ccom_id', FALSE, 'test2_chesscom'),
    (3, 3, 'test2_lc_id', FALSE, 'test2_lichess'),
    (NULL, 3, 'chess_com_user', FALSE, 'chess_com_user_not_in_service'),
    (NULL, 2, 'chess_com_bot', TRUE, 'chess_com_bot'),
    (NULL, 1, 'internal_bot', TRUE, 'bot_1');

INSERT INTO service_games("moves", "date", "metadata", "service_id", "game_id_in_service", white_player, black_player) VALUES
    (
        '1. e4 d5 2. exd5 Qxd5 3. Nc3 Qd8 { B01 Scandinavian Defense: Valencian Variation } 4. d4 Nf6 5. Nf3 g6 6. Bc4 Bg7 7. O-O O-O 8. Re1 Nbd7 9. Bg5 Nb6 10. Bd3 c6 11. Ne2 Nbd5 12. c3 Nb6 13. h3 Re8 14. Ng3 Be6 15. Qd2 Qd7 16. Bh6 Rad8 17. Ng5 Bxh6 18. N3e4 Nxe4 19. Bxe4 Bd5 20. Bd3 f6 21. h4 fxg5 22. hxg5 Bg7 23. a4 e5 24. a5 Nc4 25. Qe2 exd4 26. Bxc4 Rxe2 27. Bxd5+ Qxd5 28. Rxe2 dxc3 { White resigns. } 0-1',
        '2025-04-24T16:02:54Z',
        '{"TimeControl": "30+3"}',
        3,
        'zGsFNtCE',
        'test2_lc_id',
        'chess_com_user'
    );

INSERT INTO pgn_games("moves", "date", "metadata", "owner_id", "black_player_name", "white_player_name") VALUES
    (
        '1. d4 Nf6 2. c4 e6 3. Nf3 d5 4. g3 c6 5. Qc2 Bb4+ 6. Nbd2 O-O 7. Bg2 b6 8. O-O Ba6 9. b3 Nbd7 10. Bb2 Rc8 11. Rfd1 c5 12. a3 Bxd2 13. Nxd2 cxd4 14. Bxd4 e5 15. Bb2 dxc4 16. bxc4 Qe7 17. Qa4 Nc5 18. Qb4 Rfe8 19. Nf1 Bb7 20. Bh3 Rcd8 21. Ne3 Qc7 22. f3 a5 23. Rxd8 Rxd8 24. Qc3 Re8 25. Qc2 h6 26. Rd1 Bc6 27. Rd2 Ba4 28. Qc3 Bc6 29. Nd5 Bxd5 30. cxd5 Qd6 31. e4 Na4 32. Qc6 Qc5+ 33. Qxc5 Nxc5 34. d6 Nfd7 35. Rc2 f6 36. Bf1 Rd8 37. Bb5 Nf8 38. Rd2 Nfe6 39. a4 Nb3 40. Rd5 Nbd4 41. Bc4 Nxf3+ 42. Kf2 Nfg5 43. Ke3 Kf8 44. Ba3 Nc5 45. Bxc5 bxc5 46. Rxc5 Rxd6 47. h4 Ne6 48. Rxa5 Nc7 49. Rc5 Na6 50. Rc8+ Ke7 51. a5 Nb4 52. Rg8 g5 53. h5 Nc2+ 54. Kf2 Nb4 55. Rg7+ Kf8 56. Rb7 Nd3+ 57. Bxd3 Rxd3 58. a6 Rd2+ 59. Ke3 Ra2 60. a7 Ra3+ 61. Ke2 1-0',
        '2025-04-28T19:39:01Z',
        '{"Event": "World Championships 2025", "Site": "Krakow, Poland"}',
        1,
        'aaa',
        'bbb'
    );

INSERT INTO openings("eco", "name", "partial_fen") VALUES
    ('D11','Slav Defense: Modern Line','rnbqkbnr/pp2pppp/2p5/3p4/2PP4/5N2/PP2PPPP/RNBQKB1R b KQkq -'), -- np. 1. d4 d5 2. c4 c6 3. Nf3
    ('B00','King''s Pawn Game','rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq -'), -- np. 1. e4
    ('A00','Polish Opening: Symmetrical Variation','rnbqkbnr/p1pppppp/8/1p6/1P6/8/P1PPPPPP/RNBQKBNR w KQkq -'), -- np. 1. b4 b5
    ('C57','Italian Game: Two Knights Defense, Knight Attack','r1bqkb1r/pppp1ppp/2n2n2/4p1N1/2B1P3/8/PPPP1PPP/RNBQK2R b KQkq -'), -- np. 1. e4 e5 2. Nf3 Nc6 3. Bc4 Nf6 4. Ng5 Bc5
    ('E01','Catalan Opening: Closed','rnbqkb1r/ppp2ppp/4pn2/3p4/2PP4/6P1/PP2PPBP/RNBQK1NR b KQkq -'), -- np. 1. d4 Nf6 2. c4 e6 3. g3 d5 4. Bg2
    ('C37','King''s Gambit Accepted: Muzio Gambit, Wild Muzio Gambit','rnbqkbnr/pppp1p1p/8/8/2B1Ppp1/5N2/PPPP2PP/RNBQ1RK1 b kq -'), -- np. 1. e4 e5 2. f4 exf4 3. Nf3 g5 4. Bc4 g4 5. O-O
    ('C44','Scotch Game: Scotch Gambit, Dubois Réti Defense','r1bqkb1r/pppp1ppp/2n2n2/8/2BpP3/5N2/PPP2PPP/RNBQK2R w KQkq -'), -- np. 1. e4 e5 2. Nf3 Nc6 3. d4 exd4 4. Bc4 Nf6
    ('E68','King''s Indian Defense: Fianchetto Variation, Classical Variation','r1bq1rk1/pppn1pbp/3p1np1/4p3/2PPP3/2N2NP1/PP3PBP/R1BQ1RK1 b - -'); -- np. 1. d4 Nf6 2. c4 g6 3. Nc3 Bg7 4. Nf3 d6 5. g3 O-O 6. Bg2 Nbd7 7. O-O e5 8. e4
-- Dodanie 20 wariantów pierwszego ruchu do bazy zapewni, że każda niepusta partia będzie mieć jakiś przypisany debiut
