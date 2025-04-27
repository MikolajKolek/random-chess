-- Based on the schema exported from QuickDBD: https://www.quickdatabasediagrams.com/
-- Link to schema: https://app.quickdatabasediagrams.com/#/d/SrPe3s

CREATE OR REPLACE FUNCTION normalize_pgn(pgn varchar)
    RETURNS varchar
    IMMUTABLE
    LANGUAGE plpgsql
    AS $$
        DECLARE
            res varchar;
        BEGIN
            res := regexp_replace(pgn, '\[[^\]]*\]', '', 'g');                  -- tagi (metadane)
            res := regexp_replace(res, '\{[^}]*\}', '', 'g');                   -- komentarze multi-line
            res := regexp_replace(res, ';.+$', '', 'g');                        -- komentarze single-line
            res := regexp_replace(res, E'[\\n\\r ]+', ' ', 'g');
            res := regexp_replace(res, '\([^()]*\)', '', 'g');                  -- alternatywne ruchy
            res := regexp_replace(res, '\d+\.(\.\.)?\s*', '', 'g');             -- numery tur
            res := trim(res);
            res := regexp_replace(res, '\s*(1-0|0-1|1/2-1/2|\*)\s*$', '', 'g'); -- wynik
            RETURN res;
        END;
    $$;


CREATE TABLE "openings"
(
    "id"                    INT        PRIMARY KEY,
    "ECO"                   CHAR(3)    NOT NULL,
    "name"                  VARCHAR    NOT NULL,
    "pgn_prefix"            VARCHAR    UNIQUE NOT NULL,
    "normalized_pgn_prefix" VARCHAR    UNIQUE GENERATED ALWAYS AS (normalize_pgn(pgn_prefix)) STORED
);


CREATE TABLE "users"
(
    "id"                SERIAL          PRIMARY KEY,
    "email"             VARCHAR         UNIQUE NOT NULL,
    "password_hash"     VARCHAR(256)    NOT NULL,
    "elo"               NUMERIC         NOT NULL,
    -- Regex pochodzi z https://emailregex.com/
    CONSTRAINT valid_email CHECK (email ~* '(?:[a-z0-9!#$%&''''*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&''''*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])')
);


CREATE TABLE "game_services"
(
    "id"   int          PRIMARY KEY,
    "name" VARCHAR(256) NOT NULL
);


CREATE TABLE "service_accounts"
(
    "user_id"               INT             NULL        REFERENCES users(id) ON DELETE SET NULL,
    "service_id"            INT             NOT NULL    REFERENCES game_services(id),
    "user_id_in_service"    VARCHAR         NOT NULL,
    "display_name"          VARCHAR(256)    NOT NULL,
    "is_bot"                BOOL            NOT NULL, -- też dajemy informację jak to bot w zewnętrznym serwisie
    CONSTRAINT "pk_service_accounts" PRIMARY KEY ("service_id", "user_id_in_service")
);


CREATE TABLE "games"
(
    -- CHECK(FALSE) NO INHERIT uniemożliwia insertowanie bezpośrednio do tabeli games,
    -- zmuszając do insertowania do service_games lub pgn_games
    "id"                SERIAL          PRIMARY KEY CHECK(FALSE) NO INHERIT,
    "moves"             VARCHAR         NOT NULL,
    "date"              TIMESTAMP       NULL,
    "metadata"          JSONB           NOT NULL
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
    "owner_id"          INT             NOT NULL    REFERENCES "users" ("id"),
    "black_player_name" VARCHAR         NOT NULL,
    "white_player_name" VARCHAR         NOT NULL
) INHERITS("games");


CREATE VIEW game_openings AS (
    SELECT g.id as game_id, o.id as opening_id
    FROM openings o
    INNER JOIN games g ON(normalize_pgn(g.moves) LIKE o.normalized_pgn_prefix || '%' ESCAPE '\')
);
-- TODO: jeśli zmienimy game_openings na MATERIALIZED VIEW, to
-- utworzyć ograniczenia/indeksy (primary key i foreign keys) do game_openings


-- Dla każdego użytkownika istnieje dokładnie jeden service_account z service_id naszego serwisu (0)
-- Przechowywana tam nazwa użytkownika jest jego nazwą w naszym serwisie
-- Poniższe triggery sprawiają, że ten service_account zawsze istnieje póki użytkownik istnieje
INSERT INTO game_services(id, name) VALUES (0, 'Random Chess');

CREATE OR REPLACE FUNCTION add_default_service_to_user()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    INSERT INTO service_accounts(user_id, service_id, user_id_in_service, display_name, is_bot) VALUES (
        NEW.id, 0, NEW.id, NEW.email, FALSE
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
    -- Sprawdzenie pg_trigger_depth() sprawia, że users_delete_unlink_all_accounts może
    -- odłączyć service_account przed usunięciem użytkownika
    IF (OLD.service_id = 0) AND (old.user_id IS NOT NULL) AND (pg_trigger_depth() = 1)  THEN
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
    IF (OLD.service_id = 0) AND (old.user_id IS NOT NULL) THEN
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
