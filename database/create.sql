CREATE TYPE "game_result" AS ENUM ('white_won', 'black_won', 'draw');


-- Tabelę openings możemy bazować np. na https://github.com/lichess-org/chess-openings
CREATE TABLE "openings"
(
    "id"                SERIAL          PRIMARY KEY,
    -- ECO: https://en.wikipedia.org/wiki/Encyclopaedia_of_Chess_Openings
    "eco"               CHAR(3)         NOT NULL,
    "name"              VARCHAR(256)    NOT NULL,
    -- FEN: https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation
    -- Partial FEN - pierwsze 4 pola FEN, bez informacji o licznikach ruchów
    "partial_fen"       VARCHAR         UNIQUE NOT NULL
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
    CONSTRAINT "valid_system_account" CHECK (
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

CREATE TABLE "service_games"
(
    "id"                 SERIAL         PRIMARY KEY,
    -- kolumny wspólne dla "service_games" i "pgn_games"
    --TODO: DOES THE ARRAY BEING NOT NULL MAKE THE ELEMENTS NOT NULL??
    "moves"              VARCHAR(5)[]   NOT NULL,
    "creation_date"      TIMESTAMP      NOT NULL, -- data rozegrania partii
    "result"             GAME_RESULT    NOT NULL,
    "metadata"           JSONB          NULL,
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
    "moves"             VARCHAR(5)[]    NOT NULL,
    "creation_date"     TIMESTAMP       NOT NULL, -- data zaimportowania partii
    "result"            GAME_RESULT     NOT NULL,
    "metadata"          JSONB           NULL,
    -- kolumny występujące tylko w "pgn_games"
    "owner_id"          INT             NOT NULL    REFERENCES "users" ("id") ON DELETE CASCADE,
    "black_player_name" VARCHAR         NOT NULL,
    "white_player_name" VARCHAR         NOT NULL
);


-- Wartości "id" mogą się powtarzać, ale już pary ("id", "kind") są unikatowe
CREATE VIEW "games" AS (
    SELECT "id", 'service' AS "kind", "moves", "creation_date", "result", "metadata" FROM service_games
    UNION ALL
    SELECT "id", 'pgn' AS "kind", "moves", "creation_date", "result", "metadata" FROM pgn_games
);

CREATE VIEW "users_games" AS (
    SELECT sa."user_id" as "user_id", sg."id" as "game_id", 'service' AS "kind", "moves", "creation_date", "result", "metadata"
    FROM service_accounts sa
    JOIN service_games sg ON (sa.user_id_in_service = sg.white_player) OR (sa.user_id_in_service = sg.black_player)
    UNION
    SELECT pg."owner_id" AS "user_id", pg."id" AS "game_id", 'pgn' as "kind", "moves", "creation_date", "result", "metadata"
    FROM pgn_games pg
);

-- TODO: stworzyć view, który na podstawie tabeli openings i kolumny moves w tabeli games przypisuje każdej grze opening
/*CREATE VIEW games_openings AS (

);*/


-- Poniższe triggery sprawiają, że service_account użytkownika w naszym serwisie
-- zawsze istnieje, póki użytkownik istnieje
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
    ('test2.user@interia.pl', '1111'),
    ('makaron@studiomakaron.com', '2222');

UPDATE service_accounts SET display_name = 'test user' WHERE user_id = 1;

INSERT INTO service_accounts("user_id", "service_id", "user_id_in_service", "is_bot", "display_name") VALUES
    (2, 2, 'test_ccom_id', FALSE, 'test_chesscom'),
    (3, 2, 'test2_ccom_id', FALSE, 'test2_chesscom'),
    (3, 3, 'test2_lc_id', FALSE, 'test2_lichess'),
    (4, 2, 'makaron_ccom_id', FALSE, 'makaron_w_domu'),
    (4, 3, 'makaron_lc_id', FALSE, 'Djammnick'),
    (NULL, 2, 'chess_com_user', FALSE, 'chess_com_user_not_in_service'),
    (NULL, 3, 'lichess_user', FALSE, 'lichess_user_not_in_service'),
    (NULL, 2, 'chess_com_bot', TRUE, 'chess_com_bot'),
    (NULL, 1, 'internal_bot', TRUE, 'bot_1');

/*INSERT INTO service_games("moves", "creation_date", "result", "metadata", "service_id", "game_id_in_service", white_player, black_player) VALUES
    (
        '1. e4 d5 2. exd5 Qxd5 3. Nc3 Qd8 { B01 Scandinavian Defense: Valencian Variation } 4. d4 Nf6 5. Nf3 g6 6. Bc4 Bg7 7. O-O O-O 8. Re1 Nbd7 9. Bg5 Nb6 10. Bd3 c6 11. Ne2 Nbd5 12. c3 Nb6 13. h3 Re8 14. Ng3 Be6 15. Qd2 Qd7 16. Bh6 Rad8 17. Ng5 Bxh6 18. N3e4 Nxe4 19. Bxe4 Bd5 20. Bd3 f6 21. h4 fxg5 22. hxg5 Bg7 23. a4 e5 24. a5 Nc4 25. Qe2 exd4 26. Bxc4 Rxe2 27. Bxd5+ Qxd5 28. Rxe2 dxc3 { White resigns. } 0-1',
        '2025-04-24T16:02:54Z',
        'black_won',
        '{"TimeControl": "30+3"}',
        3,
        'zGsFNtCE',
        'test2_lc_id',
        'lichess_user'
    ),
    (
        '1. e4 e5 2. d3 Nf6 3. c3 Nc6 4. Be2 Bc5 5. Bf3 d6 6. Ne2 Be6 $2 7. Ng3 $9 O-O 8. O-O Qd7 $6 9. b4 Bb6 10. a4 a5 11. b5 Ne7 12. Nh5 $2 Ng6 $9 13. Nxf6+ $1 gxf6 14. c4 Kh8 15. Ra2 $6 Rg8 16. Nc3 Nh4 17. Kh1 Bh3 18. Nd5 $4 Nxf3 $1 19. Qxf3 $6 Bxg2+ 20. Qxg2 Rxg2 21. Kxg2 $6 Qg4+ 22. Kh1 Qf3+ 23. Kg1 Rg8+ 0-1',
        '2025-04-17T11:45:50Z',
        'black_won',
        '{"TimeControl": "3+0"}',
        2,
        'test_game_id',
        'chess_com_user',
        'makaron_ccom_id'
    ),
    (
        '1. e4 e5 2. Nf3 d6 3. Bc4 h6 4. d3 Bg4 5. Nc3 Nc6 6. h3 Bh5 7. Nxe5 Bxd1 8. Bxf7+ Ke7 9. Nd5# 1-0',
        '2020-02-01T15:20:56Z',
        'white_won',
        '{"Event": "Rated blitz game", "TimeControl": "3+2"}',
        3,
        'another_game_id',
        'makaron_lc_id',
        'lichess_user'
    );

INSERT INTO pgn_games("moves", "creation_date", "result", "metadata", "owner_id", "black_player_name", "white_player_name") VALUES
    (
        '1. d4 Nf6 2. c4 e6 3. Nf3 d5 4. g3 c6 5. Qc2 Bb4+ 6. Nbd2 O-O 7. Bg2 b6 8. O-O Ba6 9. b3 Nbd7 10. Bb2 Rc8 11. Rfd1 c5 12. a3 Bxd2 13. Nxd2 cxd4 14. Bxd4 e5 15. Bb2 dxc4 16. bxc4 Qe7 17. Qa4 Nc5 18. Qb4 Rfe8 19. Nf1 Bb7 20. Bh3 Rcd8 21. Ne3 Qc7 22. f3 a5 23. Rxd8 Rxd8 24. Qc3 Re8 25. Qc2 h6 26. Rd1 Bc6 27. Rd2 Ba4 28. Qc3 Bc6 29. Nd5 Bxd5 30. cxd5 Qd6 31. e4 Na4 32. Qc6 Qc5+ 33. Qxc5 Nxc5 34. d6 Nfd7 35. Rc2 f6 36. Bf1 Rd8 37. Bb5 Nf8 38. Rd2 Nfe6 39. a4 Nb3 40. Rd5 Nbd4 41. Bc4 Nxf3+ 42. Kf2 Nfg5 43. Ke3 Kf8 44. Ba3 Nc5 45. Bxc5 bxc5 46. Rxc5 Rxd6 47. h4 Ne6 48. Rxa5 Nc7 49. Rc5 Na6 50. Rc8+ Ke7 51. a5 Nb4 52. Rg8 g5 53. h5 Nc2+ 54. Kf2 Nb4 55. Rg7+ Kf8 56. Rb7 Nd3+ 57. Bxd3 Rxd3 58. a6 Rd2+ 59. Ke3 Ra2 60. a7 Ra3+ 61. Ke2 1-0',
        '2025-04-28T19:39:01Z',
        'white_won',
        '{"Event": "World Championships 2025", "Site": "Krakow, Poland"}',
        1,
        'aaa',
        'bbb'
    ),
    (
        '1. d4 e6 2. e4 d5 3. Nc3 c5 4. Nf3 Nc6 5. exd5 exd5 6. Be2 Nf6 7. O-O Be7 8. Bg5 O-O 9. dxc5 Be6 10. Nd4 Bxc5 11. Nxe6 fxe6 12. Bg4 Qd6 13. Bh3 Rae8 14. Qd2 Bb4 15. Bxf6 Rxf6 16. Rad1 Qc5 17. Qe2 Bxc3 18. bxc3 Qxc3 19. Rxd5 Nd4 20. Qh5 Ref8 21. Re5 Rh6 22. Qg5 Rxh3 23. Rc5 Qg3 0-1',
        '1912-01-01',
        'black_won',
        '{"Event": "DSB Congress XVIII 1912"}',
        4,
        'Stepan Levitzky',
        'Frank Marshall'
    ),
    (
        '1. e4 e5 2. Nf3 d6 3. d4 Bg4 4. dxe5 Bxf3 5. Qxf3 dxe5 6. Bc4 Nf6 7. Qb3 Qe7 8. Nc3 c6 9. Bg5 9... b5 10. Nxb5 cxb5 {This is a comment} 11. Bxb5+ Nbd7 12. O-O-O Rd8 13. Rxd7 Rxd7 14. Rd1 Qe6 15. Bxd7+ Nxd7 16. Qb8+ $3 Nxb8 17. Rd8# 1-0',
        '1858-01-01',
        'white_won',
        NULL,
        4,
        'Paul Morphy',
        'Duke Karl / Count Isouard'
    ),
    (
        '1. e4 d6 2. d4 Nf6 3. Nc3 g6 4. Be3 Bg7 5. Qd2 c6 6. f3 b5 7. Nge2 Nbd7 8. Bh6 Bxh6 9. Qxh6 Bb7 10. a3 e5 11. O-O-O Qe7 12. Kb1 a6 13. Nc1 O-O-O 14. Nb3 exd4 15. Rxd4 c5 16. Rd1 Nb6 17. g3 Kb8 18. Na5 Ba8 19. Bh3 d5 20. Qf4+ Ka7 21. Rhe1 d4 22. Nd5 Nbxd5 23. exd5 Qd6 24. Rxd4 cxd4 25. Re7+ Kb6 26. Qxd4+ Kxa5 27. b4+ Ka4 28. Qc3 Qxd5 29. Ra7 Bb7 30. Rxb7 Qc4 31. Qxf6 Kxa3 32. Qxa6+ Kxb4 33. c3+ Kxc3 34. Qa1+ Kd2 35. Qb2+ Kd1 36. Bf1 Rd2 37. Rd7 Rxd7 38. Bxc4 bxc4 39. Qxh8 Rd3 40. Qa8 c3 41. Qa4+ Ke1 42. f4 f5 43. Kc1 Rd2 44. Qa7 1-0',
        '1999-01-01',
        'white_won',
        '{"Event": "Hoogovens Wijk aan Zee Chess Tournament 1999"}',
        4,
        'Garry Kasparov',
        'Veselin Topalov'
    );*/

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

INSERT INTO service_accounts("user_id", "service_id", "user_id_in_service", "is_bot", "display_name") VALUES
    (NULL, 1, 'stockfish-easy', TRUE, 'Stockfish (Easy)'),
    (NULL, 1, 'stockfish-medium', TRUE, 'Stockfish (Medium)'),
    (NULL, 1, 'stockfish-hard', TRUE, 'Stockfish (Hard)'),
    (NULL, 1, 'stockfish-impossible', TRUE, 'Stockfish (Impossible)');