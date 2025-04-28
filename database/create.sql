-- Tabelę openings możemy bazować np. na https://github.com/lichess-org/chess-openings
CREATE TABLE "openings"
(
    "id"                    SERIAL     PRIMARY KEY,
    -- ECO: https://en.wikipedia.org/wiki/Encyclopaedia_of_Chess_Openings
    "eco"                   CHAR(3)    NOT NULL,
    "name"                  VARCHAR    NOT NULL,
    -- EPD: https://www.chessprogramming.org/Extended_Position_Description
    "epd"                   VARCHAR    UNIQUE NOT NULL
);


CREATE TABLE "users"
(
    "id"                SERIAL          PRIMARY KEY,
    "email"             VARCHAR         UNIQUE NOT NULL,
    "password_hash"     VARCHAR         NOT NULL,
    "elo"               NUMERIC         NOT NULL DEFAULT 1500,
    -- Regex pochodzi z https://emailregex.com/
    CONSTRAINT valid_email CHECK (email ~* '(?:[a-z0-9!#$%&''''*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&''''*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])')
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
    "user_id"            INT          NULL REFERENCES users (id) ON DELETE SET NULL,
    "service_id"         INT          NOT NULL REFERENCES game_services (id),
    "user_id_in_service" VARCHAR      NOT NULL,
    "display_name"       VARCHAR(256) NOT NULL,
    "is_bot"             BOOL         NOT NULL,
    CONSTRAINT "pk_service_accounts" PRIMARY KEY ("service_id", "user_id_in_service"),
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


CREATE TABLE "games"
(
    -- CHECK(FALSE) NO INHERIT uniemożliwia insertowanie bezpośrednio do tabeli games,
    -- zmuszając do insertowania do service_games lub pgn_games
    "id"                SERIAL          PRIMARY KEY CHECK(FALSE) NO INHERIT,
    "moves"             VARCHAR         NOT NULL,
    "date"              TIMESTAMP       NULL,
    "metadata"          JSONB           NOT NULL
    -- TODO: napisać funkcję generującą listę pozycji w formacie EPD dla gry
    -- "epd_positions"     VARCHAR[]       GENERATED ALWAYS AS ()
);

CREATE TABLE "service_games"
(
    "game_id_in_service" VARCHAR        NOT NULL,
    "service_id"         INT            NOT NULL    REFERENCES "game_services" ("id"),
    "white_player"       VARCHAR        NOT NULL,
    "black_player"       VARCHAR        NOT NULL,
    CONSTRAINT "fk_service_games_service_id_white_player" FOREIGN KEY ("service_id", "white_player")
        REFERENCES "service_accounts" ("service_id", "user_id_in_service"),
    CONSTRAINT "fk_service_games_service_id_black_player" FOREIGN KEY ("service_id", "black_player")
        REFERENCES "service_accounts" ("service_id", "user_id_in_service")
) INHERITS("games");

CREATE TABLE "pgn_games"
(
    "owner_id"          INT             NOT NULL    REFERENCES "users" ("id") ON DELETE CASCADE,
    "black_player_name" VARCHAR         NOT NULL,
    "white_player_name" VARCHAR         NOT NULL
) INHERITS("games");


-- TODO: stworzyć view który na podstawie tabeli openings i epd_positions w games przypisuje każdej grze opening
/*CREATE VIEW game_openings AS (

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


INSERT INTO game_services(id, name) VALUES
    (1, 'chess.com'),
    (2, 'lichess.org');

-- INSERT INTO service_accounts("service_id", "user_id_in_service", "is_bot", "display_name") VALUES
--      ()

-- INSERT INTO service_games("moves", "date", service_id, "game_id_in_service", white_player, black_player) VALUES
--     (
--         '1. e4 d5 2. exd5 Qxd5 3. Nc3 Qd8 { B01 Scandinavian Defense: Valencian Variation } 4. d4 Nf6 5. Nf3 g6 6. Bc4 Bg7 7. O-O O-O 8. Re1 Nbd7 9. Bg5 Nb6 10. Bd3 c6 11. Ne2 Nbd5 12. c3 Nb6 13. h3 Re8 14. Ng3 Be6 15. Qd2 Qd7 16. Bh6 Rad8 17. Ng5 Bxh6 18. N3e4 Nxe4 19. Bxe4 Bd5 20. Bd3 f6 21. h4 fxg5 22. hxg5 Bg7 23. a4 e5 24. a5 Nc4 25. Qe2 exd4 26. Bxc4 Rxe2 27. Bxd5+ Qxd5 28. Rxe2 dxc3 { White resigns. } 0-1',
--         '2025-04-24T16:02:54Z',
--         2,
--         'zGsFNtCE',
--         'TLTLTLTLTLTLTLTLTIT',
--         'chess-art-us'
--     );

/*INSERT INTO users(email, password_hash, elo) VALUES ('test@[1.1.1.1]', '123', 0)
INSERT INTO pgn_games("id", "moves", "date", "owner_id", "black_player_name", "white_player_name", "metadata") VALUES
                                             (1, '69', '2003-04-12 04:05:06', 1, 'a', 'b', '{}')
*/
