CREATE TYPE "game_result_type" AS (
    "game_end_type" VARCHAR,
    "game_end_reason" VARCHAR
);

CREATE DOMAIN "game_result" AS "game_result_type"
    CHECK(
        (
            ((VALUE).game_end_type IN ('1-0', '0-1'))
            AND
            ((VALUE).game_end_reason IN ('UNKNOWN', 'TIMEOUT', 'CHECKMATE', 'RESIGNATION', 'ABANDONMENT', 'DEATH'))
        )
        OR
        (
            ((VALUE).game_end_type = '1/2-1/2')
                AND
            ((VALUE).game_end_reason IN ('UNKNOWN', 'TIMEOUT_VS_INSUFFICIENT_MATERIAL', 'INSUFFICIENT_MATERIAL', 'THREEFOLD_REPETITION', 'FIFTY_MOVE_RULE', 'STALEMATE'))
        )
    );

CREATE TYPE "clock_settings_type" AS (
	"starting_time" INTERVAL,
	"move_increase" INTERVAL
);

CREATE DOMAIN "clock_settings" AS "clock_settings_type"
	CHECK(
        (
            ((VALUE)."starting_time" IS NULL) AND
            ((VALUE)."move_increase" IS NULL)
        )
        OR (
            ((VALUE)."starting_time" IS NOT NULL) AND
            ((VALUE)."move_increase" IS NOT NULL) AND
            ((VALUE)."starting_time" >= INTERVAL '0 seconds') AND
            ((VALUE)."move_increase" >= INTERVAL '0 seconds')
        )
	);


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
    -- Regex pochodzi z https://emailregex.com/
    CHECK (email ~* '(?:[a-z0-9!#$%&''''*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&''''*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])')
);


CREATE TABLE "game_services"
(
    "id"   SERIAL       PRIMARY KEY,
    "name" VARCHAR(256) UNIQUE NOT NULL
);
INSERT INTO game_services("id", "name") VALUES (1, 'Random Chess');
SELECT setval('game_services_id_seq', 1, true);


-- Dla każdego użytkownika istnieje dokładnie jeden service_account z service_id naszego serwisu (1)
-- Przechowywana tam nazwa użytkownika jest jego nazwą w naszym serwisie
CREATE TABLE "service_accounts"
(
    "user_id"            INT          NULL REFERENCES users ("id") ON DELETE SET NULL,
    "service_id"         INT          NOT NULL REFERENCES game_services ("id"),
    "user_id_in_service" VARCHAR      NOT NULL,
    "token"              VARCHAR      NULL,
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
    ),
    CONSTRAINT "valid_token" CHECK (
        ("token" IS NULL) = ("service_id" = 1 OR "user_id" IS NULL)
    )
);


-- Funkcja parsująca szachownicę z FEN na format łatwiejszy do modyfikacji
CREATE OR REPLACE FUNCTION fen_to_board(
    fen VARCHAR
) RETURNS VARCHAR AS
$$
DECLARE
    ret VARCHAR := '';
    c CHAR;
    i INT := 1;
    j INT := 0;
BEGIN
    WHILE(i <= length(fen)) LOOP
        c := substr(fen, i, 1);
        IF(c ~ '[0-9]') THEN
            WHILE(j < c::integer) LOOP
                ret:=ret||'e'; -- 'e' represents empty square
                j:=j+1;
            END LOOP;
            j:=0;
        ELSE
            ret:=ret||c;
        END IF;
        i:=i+1;
    END LOOP;
    RETURN ret;
END;
$$
LANGUAGE plpgsql IMMUTABLE;

-- Funkcja parsująca szachownicę z lokalnego formatu na format FEN
CREATE OR REPLACE FUNCTION board_to_fen(
    board VARCHAR
) RETURNS VARCHAR AS
$$
DECLARE
    ret VARCHAR := '';
    c CHAR;
    empty INT := 0;
    i INT := 1;
BEGIN
    WHILE(i <= length(board)) LOOP
        c:=substr(board,i,1);
        IF(c='e') THEN empty:=empty+1;
        ELSE
            IF(empty!=0) THEN
                ret=ret||empty::CHAR;
                empty:=0;
            END IF;
            ret=ret||c;
        END IF;
        i:=i+1;
    END LOOP;
    IF(empty!=0) THEN ret=ret||empty::CHAR; END IF;
    RETURN ret;
END;
$$
LANGUAGE plpgsql IMMUTABLE;

-- Funkcja przekształcająca pole szachownicy na jego miejsce w tablicy
CREATE OR REPLACE FUNCTION square_to_id(
    square VARCHAR(2)
) RETURNS INTEGER AS
$$
DECLARE
BEGIN
    RETURN (8-substr(square, 2, 1)::integer)*9+ascii(substr(square, 1, 1))-ascii('a')+1;
END;
$$
LANGUAGE plpgsql IMMUTABLE;

-- Funkcja przekształcająca miejsce w tablicy na pole szachownicy
CREATE OR REPLACE FUNCTION id_to_square(
    id INTEGER
) RETURNS VARCHAR(2) AS
$$
DECLARE
BEGIN
    RETURN chr(ascii('a')+(id%9-1))||(8-id/9)::char;
END;
$$
LANGUAGE plpgsql IMMUTABLE;

-- Funckcja zwraca figurę na danym polu
CREATE OR REPLACE FUNCTION get_piece_at(
    board VARCHAR,
    square VARCHAR(2)
) RETURNS CHAR AS
$$
DECLARE
BEGIN
    IF(substr(square, 1, 1) < 'a' OR substr(square, 1, 1) > 'h') THEN RETURN 'e'; END IF;
    RETURN substr(board, square_to_id(square), 1);
END;
$$
LANGUAGE plpgsql IMMUTABLE;

-- Funkcja dodaje figurę do danego pola w board (lub usuwa po przekazaniu 'e')
CREATE OR REPLACE FUNCTION place_piece(
    board VARCHAR,
    square VARCHAR(2),
    piece VARCHAR
) RETURNS VARCHAR AS
$$
DECLARE
    id INTEGER := square_to_id(square);
BEGIN
    RETURN substr(board, 1, id-1)||piece||substr(board, id+1, length(board)-id);
END;
$$
LANGUAGE plpgsql IMMUTABLE;

-- Funkcja która usuwa daną literkę z ciągu znaków - w celu modyfikacji castling rights.
CREATE OR REPLACE FUNCTION remove_letter(
    str VARCHAR,
    letter CHAR
) RETURNS VARCHAR AS
$$
DECLARE
    new_str VARCHAR := '';
    i INTEGER := 1;
BEGIN
    WHILE(i <= length(str)) LOOP
        IF(substr(str, i, 1) != letter) THEN
            new_str := new_str||substr(str, i, 1);
        END IF;
        i := i+1;
    END LOOP;
    IF(new_str = '') THEN
        RETURN '-';
    END IF;
    RETURN new_str;
END;
$$
LANGUAGE plpgsql IMMUTABLE;

-- Funkcja aplikująca ruch do partial FEN
CREATE OR REPLACE FUNCTION apply_move(
    fen VARCHAR,
    move VARCHAR(5)
) RETURNS VARCHAR AS
$$
DECLARE
    board VARCHAR := fen_to_board(split_part(fen, ' ', 1));
    newBoard VARCHAR;
    piece CHAR;
    from_square VARCHAR(2) = substr(move, 1, 2);
    to_square VARCHAR(2) = substr(move, 3, 2);
    color CHAR := split_part(fen, ' ', 2);
    castling_rights VARCHAR := split_part(fen, ' ', 3);
    en_passant VARCHAR := split_part(fen, ' ', 4);
    promote_piece CHAR;
BEGIN
    -- Aplikowanie ruchu
    newBoard := board;
    piece := get_piece_at(newBoard, from_square);
    newBoard := place_piece(newBoard, from_square, 'e');
    newBoard := place_piece(newBoard, to_square, piece);

    -- Zmiana koloru przy ruchu
    IF(color = 'w') THEN
        color := 'b';
    ELSE
        color := 'w';
    END IF;

    -- Promocja
    IF(length(move) = 5) THEN
        promote_piece := substr(move, 5, 1);
        IF(piece = LOWER(piece)) THEN
            newBoard := place_piece(newBoard, to_square, LOWER(promote_piece));
        ELSE
            newBoard := place_piece(newBoard, to_square, UPPER(promote_piece));
        END IF;
    END IF;

    -- Capturing en passant
    IF(LOWER(piece) = 'p') THEN
        IF(get_piece_at(board, to_square)='e') THEN
            IF(substr(move, 1, 1) != substr(move, 3, 1)) THEN
                IF(piece = LOWER(piece)) THEN
                    newBoard := place_piece(newBoard, id_to_square(square_to_id(to_square)-9), 'e');
                ELSE
                    newBoard := place_piece(newBoard, id_to_square(square_to_id(to_square)+9), 'e');
                END IF;
            END IF;
        END IF;
    END IF;

    -- Replacing rook in castling and castling rights in king move
    IF(LOWER(piece) = 'k') THEN
        IF(from_square = 'e8') THEN
            castling_rights := remove_letter(castling_rights, 'k');
            castling_rights := remove_letter(castling_rights, 'q');
            IF(to_square = 'c8') THEN
                newBoard := place_piece(newBoard, 'a8', 'e');
                newBoard := place_piece(newBoard, 'd8', 'r');
            END IF;
            IF(to_square = 'g8') THEN
                newBoard := place_piece(newBoard, 'h8', 'e');
                newBoard := place_piece(newBoard, 'f8', 'r');
            END IF;
        END IF;
        IF(from_square = 'e1') THEN
            castling_rights := remove_letter(castling_rights, 'K');
            castling_rights := remove_letter(castling_rights, 'Q');
            IF(to_square = 'c1') THEN
                newBoard := place_piece(newBoard, 'a1', 'e');
                newBoard := place_piece(newBoard, 'd1', 'R');
            END IF;
            IF(to_square = 'g1') THEN
                newBoard := place_piece(newBoard, 'h1', 'e');
                newBoard := place_piece(newBoard, 'f1', 'R');
            END IF;
        END IF;
    END IF;

    -- Removing castling rights on rook move
    IF(from_square = 'a1' OR to_square = 'a1') THEN
        castling_rights := remove_letter(castling_rights, 'Q');
    END IF;
    IF(from_square = 'a8' OR to_square = 'a8') THEN
        castling_rights := remove_letter(castling_rights, 'q');
    END IF;
    IF(from_square = 'h1' OR to_square = 'h1') THEN
        castling_rights := remove_letter(castling_rights, 'K');
    END IF;
    IF(from_square = 'h8' OR to_square = 'h8') THEN
        castling_rights := remove_letter(castling_rights, 'k');
    END IF;

    en_passant := '-';
    IF(LOWER(piece) = 'p') THEN
        IF(substr(move, 2, 1) = '2' AND substr(move, 4, 1) = '4') THEN
            IF(get_piece_at(board, (chr(ascii(substr(move, 1, 1))-1)||'4')::VARCHAR) = 'p'
                OR get_piece_at(board, (chr(ascii(substr(move, 1, 1))+1)||'4')::VARCHAR) = 'p') THEN
                en_passant := substr(move, 1, 1)||'3';
            END IF;
        END IF;
        IF(substr(move, 2, 1) = '7' AND substr(move, 4, 1) = '5') THEN
            IF(get_piece_at(board, (chr(ascii(substr(move, 1, 1))-1)||'5')::VARCHAR) = 'P'
                OR get_piece_at(board, (chr(ascii(substr(move, 1, 1))+1)||'5')::VARCHAR) = 'P') THEN
                en_passant := substr(move, 1, 1)||'6';
            END IF;
        END IF;
    END IF;

    RETURN board_to_fen(newBoard)||' '||color||' '||castling_rights||' '||en_passant;
END;
$$
LANGUAGE plpgsql IMMUTABLE;

-- Funkcja generująca tablicę partial FEN zaczynając od pozycji startowej i potem po każdym ruchu
CREATE OR REPLACE FUNCTION generate_fen_array(
    starting_position VARCHAR,
    moves VARCHAR(5)[]
) RETURNS VARCHAR[] AS
$$
DECLARE
    result_fen_array VARCHAR[] := '{}';
    start VARCHAR := array_to_string(trim_array(string_to_array(starting_position, ' '), 2), ' ');
    last_fen VARCHAR := start;
    elem VARCHAR(5);
BEGIN
    result_fen_array := array_append(result_fen_array, start);
    FOREACH elem IN ARRAY moves
    LOOP
        last_fen := apply_move(last_fen, elem);
        result_fen_array := array_append(result_fen_array, last_fen);
    END LOOP;
    RETURN result_fen_array;
END;
$$
LANGUAGE plpgsql IMMUTABLE;


-- Tworzymy dwie tabele reprezentujące rozegrane gry: service_games i pgn_games.
-- Niektóre z ich kolumn się pokrywają.
-- Ze względu na ograniczenia mechanizmów polimorficzności w PostgreSQL zdecydowaliśmy,
-- że jest to najlepsze rozwiązanie. Opis innych rozważanych rozwiązań dołączamy
-- do zgłoszenia projektu.
--
-- "id" w tabelach service_games i pgn_games są unikalne tylko w obrębie danej tabeli.

CREATE TABLE "service_games"
(
    "id"                 SERIAL             PRIMARY KEY,
    -- kolumny wspólne dla "service_games" i "pgn_games"
    "moves"              VARCHAR(5)[]       NOT NULL CHECK(array_position(moves, NULL) IS NULL),
    "starting_position"  VARCHAR(100)       NOT NULL,
    "partial_fens"       VARCHAR[]          GENERATED ALWAYS AS (generate_fen_array(starting_position, moves)) STORED, -- FEN pozycji po każdym ruchu od pozycji startowej
    "creation_date"      TIMESTAMPTZ        NOT NULL, -- data rozegrania partii
    "result"             GAME_RESULT        NOT NULL,
    "metadata"           JSONB              NULL,
    "clock"              "clock_settings"   NULL,
    -- kolumny występujące tylko w "service_games"
    "game_id_in_service" VARCHAR            NULL,
    "service_id"         INT                NOT NULL    REFERENCES "game_services" ("id"),
    "white_player"       VARCHAR            NOT NULL,
    "black_player"       VARCHAR            NOT NULL,
    "is_ranked"          BOOLEAN            NOT NULL,
    -- Partie rozegrane w naszym serwisie mają "game_id_in_service" ustawione na NULL,
    -- a w innych serwisach zawsze mają ustawioną wartość oraz nie liczą się do rankingów.
    CHECK (CASE
        WHEN "service_id" = 1 THEN "game_id_in_service" IS NULL
        ELSE ("game_id_in_service" IS NOT NULL AND "is_ranked" = FALSE)
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
    "starting_position" VARCHAR(100)    NOT NULL,
    "partial_fens"      VARCHAR[]       GENERATED ALWAYS AS (generate_fen_array(starting_position, moves)) STORED, -- FEN pozycji po każdym ruchu od pozycji startowej
    "creation_date"     TIMESTAMPTZ     NOT NULL, -- data zaimportowania partii
    "result"            GAME_RESULT     NOT NULL,
    "metadata"          JSONB           NULL,
    "clock"              "clock_settings"   NULL,
    -- kolumny występujące tylko w "pgn_games"
    "owner_id"          INT             NOT NULL    REFERENCES "users" ("id") ON DELETE CASCADE,
    "black_player_name" VARCHAR         NOT NULL,
    "white_player_name" VARCHAR         NOT NULL
);

-- Wartości "id" mogą się powtarzać, ale już pary ("id", "kind") są unikatowe
CREATE VIEW "games" AS (
    SELECT "id",
           'service' AS "kind",
           "starting_position",
           "moves",
           "partial_fens",
           "creation_date",
           "result",
           "metadata",
           "clock",
           "game_id_in_service",
           "service_id",
           "white_player" AS "white_service_account",
           "black_player" AS "black_service_account",
           "is_ranked",
           NULL AS "pgn_owner_id",
           NULL AS "pgn_black_player_name",
           NULL AS "pgn_white_player_name"
    FROM service_games
    UNION ALL
    SELECT "id",
           'pgn' AS "kind",
           "starting_position",
           "moves",
           "partial_fens",
           "creation_date",
           "result",
           "metadata",
           "clock",
           NULL AS "game_id_in_service",
           NULL AS "service_id",
           NULL AS "white_service_account",
           NULL AS "black_service_account",
           NULL AS "is_ranked",
           "owner_id" AS "pgn_owner_id",
           "black_player_name" AS "pgn_black_player_name",
           "white_player_name" AS "pgn_white_player_name"
    FROM pgn_games
);

CREATE VIEW "users_games" AS (
    SELECT sa."user_id" as "user_id", sg."id" as "game_id", 'service' AS "kind", "moves", "creation_date", "result", "metadata"
    FROM service_accounts sa
    JOIN service_games sg ON (sa.user_id_in_service = sg.white_player) OR (sa.user_id_in_service = sg.black_player)
    UNION
    SELECT pg."owner_id" AS "user_id", pg."id" AS "game_id", 'pgn' as "kind", "moves", "creation_date", "result", "metadata"
    FROM pgn_games pg
);

CREATE OR REPLACE FUNCTION detect_opening(
    partial_fens VARCHAR[]
) RETURNS RECORD AS
$$
DECLARE
    my_partial_fen VARCHAR;
    opening_id INTEGER := NULL;
    move_no INTEGER := 0;
    ret_val RECORD := NULL;
BEGIN
    FOREACH my_partial_fen IN ARRAY partial_fens LOOP
        IF EXISTS(
            SELECT *
            FROM openings o
            WHERE my_partial_fen=o.partial_fen
        ) THEN
            opening_id := (SELECT o.id
                FROM openings o
                WHERE my_partial_fen=o.partial_fen
                LIMIT 1
            );
            SELECT opening_id, move_no INTO ret_val;
        END IF;
        move_no := move_no + 1;
    END LOOP;
    RETURN ret_val;
END;
$$
LANGUAGE plpgsql;

CREATE VIEW games_openings AS (
    SELECT g.id AS game_id, g.kind AS kind, opening_id, move_no
    FROM games g, LATERAL detect_opening(partial_fens) AS (opening_id INTEGER, move_no INTEGER)
);


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


CREATE TABLE rankings(
    "id"                    SERIAL      PRIMARY KEY,
    "name"                  VARCHAR     NOT NULL,
    -- Inclusive:
    "playtime_min"          INTERVAL    NOT NULL    CHECK ("playtime_min" >= '0 seconds'::INTERVAL),
    -- Exclusive:
    "playtime_max"          INTERVAL    NULL        CHECK ("playtime_max" > '0 seconds'::INTERVAL),
    "extra_move_multiplier" INT         NOT NULL    CHECK ("extra_move_multiplier" >= 0),
    "starting_elo"          NUMERIC     NOT NULL    CHECK ("starting_elo" > 0),
    "include_bots"          BOOLEAN     NOT NULL,
    "k_factor"              NUMERIC     NOT NULL,

    CONSTRAINT "playtime_valid" CHECK ("playtime_max" IS NULL OR "playtime_min" <= "playtime_max")
);


CREATE TABLE elo_history(
    "id"                    SERIAL      PRIMARY KEY,
    "service_id"            INT         NOT NULL    REFERENCES "game_services" ("id")
        CHECK ("service_id" = 1),
    "user_id_in_service"    VARCHAR     NOT NULL,
    "ranking_id"            INT         NOT NULL    REFERENCES "rankings" ("id"),
    "game_id"               INT         NOT NULL    REFERENCES "service_games" ("id"),
    "elo"                   NUMERIC     NOT NULL,
    "previous_entry"        INT         NULL,

    -- Make sure that a single elo history entry is not the previous entry more than once
    UNIQUE NULLS NOT DISTINCT ("service_id", "user_id_in_service", "ranking_id", "previous_entry"),

    -- A single game can only influence a player's elo once
    UNIQUE("service_id", "user_id_in_service", "ranking_id", "game_id"),

    UNIQUE("service_id", "user_id_in_service", "ranking_id", "id"),
    FOREIGN KEY("service_id", "user_id_in_service", "ranking_id", "previous_entry")
        REFERENCES "elo_history"("service_id", "user_id_in_service", "ranking_id", "id"),

    FOREIGN KEY ("service_id", "user_id_in_service")
        REFERENCES "service_accounts" ("service_id", "user_id_in_service")
);

-- Table mapping games from service_games to the rankings in which the game is rated
CREATE VIEW games_rankings AS
SELECT
    "service_games"."id" AS "game_id",
    "rankings"."id" AS "ranking_id"
FROM "service_games"
INNER JOIN "service_accounts" white_accounts ON (
    white_accounts."service_id" = "service_games"."service_id" AND
    white_accounts."user_id_in_service" = "service_games"."white_player"
)
INNER JOIN "service_accounts" black_accounts ON (
    black_accounts."service_id" = "service_games"."service_id" AND
    black_accounts."user_id_in_service" = "service_games"."black_player"
)
CROSS JOIN "rankings"
WHERE
    "service_games"."is_ranked" AND
    ("service_games"."clock")."starting_time" IS NOT NULL AND
    ("service_games"."clock")."move_increase" IS NOT NULL AND
    (
        ("service_games"."clock")."starting_time" +
        "rankings"."extra_move_multiplier" * ("service_games"."clock")."move_increase"
    ) >= "rankings"."playtime_min" AND
    (
        "rankings"."playtime_max" IS NULL OR
        (
            ("service_games"."clock")."starting_time" +
            "rankings"."extra_move_multiplier" * ("service_games"."clock")."move_increase"
        ) < "rankings"."playtime_max"
    ) AND
    (
        "rankings"."include_bots" OR
        (white_accounts."is_bot" = FALSE AND black_accounts."is_bot" = FALSE)
    )
;


CREATE FUNCTION ranking_at_timestamp(t TIMESTAMPTZ)
    RETURNS TABLE(service_id INT, user_id_in_service VARCHAR, ranking_id INT, elo NUMERIC, elo_history_id INT)
AS
$$
BEGIN
    RETURN QUERY SELECT
        DISTINCT ON (sa.service_id, sa.user_id_in_service, r.id)
        sa.service_id,
        sa.user_id_in_service,
        r.id AS "ranking_id",
        COALESCE(eh.elo, r.starting_elo) AS "elo",
        "eh".id AS "elo_history_id"
    FROM service_accounts sa
    CROSS JOIN rankings r
    LEFT JOIN elo_history eh ON (r.id = eh.ranking_id AND sa.user_id_in_service = eh.user_id_in_service)
    LEFT JOIN service_games sg ON (eh.game_id = sg.id)
    WHERE (sa.service_id = 1) AND (r.include_bots OR NOT sa.is_bot) AND (sg.creation_date <= t OR sg IS NULL)
    ORDER BY sa.service_id, sa.user_id_in_service, r.id, sg.creation_date DESC;
END;
$$
LANGUAGE plpgsql;

CREATE FUNCTION ranking_with_placement_at_timestamp(t TIMESTAMPTZ, ranking INT)
    RETURNS TABLE(placement INT, service_id INT, user_id_in_service VARCHAR, ranking_id INT, elo INT, elo_history_id INT)
AS
$$
BEGIN
    RETURN QUERY SELECT
        (rank() over (ORDER BY rt.elo::int DESC))::int,
        rt.service_id,
        rt.user_id_in_service,
        rt.ranking_id,
        rt.elo::int,
        rt.elo_history_id
    FROM ranking_at_timestamp(t) rt
    WHERE rt.ranking_id = ranking
    ORDER BY rt.elo::int DESC;
END;
$$
LANGUAGE plpgsql;

CREATE VIEW current_ranking AS(
    SELECT *
    FROM ranking_at_timestamp(CURRENT_TIMESTAMP)
);

CREATE PROCEDURE update_ranking_after_game(
    service_game_id INT,
    ranking_id_to_update INT
) AS
$$
DECLARE
    -- Variable names taken from https://en.wikipedia.org/wiki/Elo_rating_system#Mathematical_details
    game_end_type VARCHAR;
    current_black_elo NUMERIC;
    current_white_elo NUMERIC;
    previous_black_entry INT;
    previous_white_entry INT;
    k_factor_var NUMERIC;
    black_score NUMERIC;
    white_score NUMERIC;
    Q_black NUMERIC;
    Q_white NUMERIC;
    expected_black_value NUMERIC;
    expected_white_value NUMERIC;
BEGIN
    IF service_game_id IS NULL OR ranking_id_to_update IS NULL THEN
        RETURN;
    END IF;

    --TODO: fix SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;

    SELECT cr_black.elo, cr_black.elo_history_id, (sg.result).game_end_type
    INTO current_black_elo, previous_black_entry, game_end_type
    FROM service_games sg
    JOIN service_accounts sa_black ON(
        sg.service_id = sa_black.service_id AND
        sg.black_player = sa_black.user_id_in_service
    )
    JOIN current_ranking cr_black ON(
        sa_black.service_id = cr_black.service_id AND
        sa_black.user_id_in_service = cr_black.user_id_in_service AND
        cr_black.ranking_id = ranking_id_to_update
    )
    WHERE sg.id = service_game_id;

    SELECT cr_white.elo, cr_white.elo_history_id
    INTO current_white_elo, previous_white_entry
    FROM service_games sg
    JOIN service_accounts sa_white ON(
        sg.service_id = sa_white.service_id AND
        sg.white_player = sa_white.user_id_in_service
    )
    JOIN current_ranking cr_white ON(
        sa_white.service_id = cr_white.service_id AND
        sa_white.user_id_in_service = cr_white.user_id_in_service AND
        cr_white.ranking_id = ranking_id_to_update
    )
    WHERE sg.id = service_game_id;

    k_factor_var := (SELECT k_factor FROM rankings WHERE id = ranking_id_to_update);

    IF game_end_type = '1/2-1/2' THEN
        black_score := 0.5;
        white_score := 0.5;
    ELSE
        IF game_end_type = '1-0' THEN
            black_score := 0;
            white_score := 1;
        ELSE
            black_score := 1;
            white_score := 0;
        END IF;
    END IF;

    Q_black := 10^(current_black_elo / 400);
    Q_white := 10^(current_white_elo / 400);

    expected_black_value := Q_black / (Q_black + Q_white);
    expected_white_value := Q_white / (Q_black + Q_white);

    INSERT INTO elo_history(service_id, user_id_in_service, ranking_id, game_id, elo, previous_entry) VALUES
        (
             1,
             (SELECT black_player FROM service_games WHERE id = service_game_id),
             ranking_id_to_update,
             service_game_id,
             current_black_elo + k_factor_var * (black_score - expected_black_value),
             previous_black_entry
        ),
        (
            1,
            (SELECT white_player FROM service_games WHERE id = service_game_id),
            ranking_id_to_update,
            service_game_id,
            current_white_elo + k_factor_var * (white_score - expected_white_value),
            previous_white_entry
        );
END;
$$
LANGUAGE plpgsql;

CREATE PROCEDURE update_elo_after_game(
    service_game_id INT
) AS
$$
DECLARE
    ranking_id INT;
BEGIN
    FOR ranking_id IN
        SELECT gr.ranking_id
        FROM games_rankings gr
        WHERE gr.game_id = service_game_id
    LOOP
        CALL update_ranking_after_game(service_game_id, ranking_id);
    END LOOP;
END;
$$
LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE recalculate_ranking(ranking_id int)
LANGUAGE plpgsql
AS
$$
DECLARE
    game_id INT;
BEGIN
    --TODO: fix SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

    DELETE FROM elo_history
    WHERE elo_history.ranking_id = recalculate_ranking.ranking_id;

    FOR game_id IN
        SELECT service_games.id
        FROM service_games
        INNER JOIN games_rankings ON (service_games.id = games_rankings.game_id)
        WHERE games_rankings.ranking_id = recalculate_ranking.ranking_id
        ORDER BY creation_date ASC
    LOOP
        CALL update_ranking_after_game(game_id, ranking_id);
    END LOOP;
END;
$$;

CREATE OR REPLACE FUNCTION recalculate_ranking_on_update()
RETURNS trigger
LANGUAGE plpgsql
AS
$$
BEGIN
    CALL recalculate_ranking(NEW.id);
    RETURN NEW;
END;
$$;

CREATE OR REPLACE TRIGGER rankings_insert_update_recalculate
    AFTER INSERT OR UPDATE ON rankings
    FOR EACH ROW
EXECUTE PROCEDURE recalculate_ranking_on_update();


CREATE OR REPLACE FUNCTION update_rankings_on_game_insert()
RETURNS trigger
LANGUAGE plpgsql
AS
$$
DECLARE
    ranking_id INT;
BEGIN
    FOR ranking_id IN
        SELECT games_rankings.ranking_id
        FROM games_rankings
        WHERE games_rankings.game_id = NEW.id
    LOOP
        CALL update_ranking_after_game(NEW.id, ranking_id);
    END LOOP;
    RETURN NEW;
END;
$$;

CREATE OR REPLACE TRIGGER service_games_insert_update_rankings
    AFTER INSERT ON service_games
    FOR EACH ROW
EXECUTE PROCEDURE update_rankings_on_game_insert();


CREATE OR REPLACE FUNCTION prevent_game_deletion()
RETURNS TRIGGER
LANGUAGE plpgsql
AS
$$
BEGIN
    RAISE EXCEPTION 'Deleting service games is forbidden - tried to delete game %', OLD.id;
END;
$$;

CREATE OR REPLACE FUNCTION prevent_significant_game_changes()
RETURNS TRIGGER
LANGUAGE plpgsql
AS
$$
BEGIN
    IF (OLD.result).game_end_type <> (NEW.result).game_end_type THEN
        RAISE EXCEPTION 'Modifying game_end_type in a service game is forbidden - tried to modify game %', OLD.id;
    END IF;

    IF
        (OLD.clock).starting_time IS NULL <> (NEW.clock).starting_time IS NULL OR
        (
            (NEW.clock).starting_time IS NOT NULL AND
            OLD.clock <> NEW.clock
        )
    THEN
        RAISE EXCEPTION 'Modifying clock in a service game is forbidden - tried to modify game %', OLD.id;
    END IF;

    IF OLD.white_player <> NEW.white_player THEN
        RAISE EXCEPTION 'Modifying white_player in a service game is forbidden - tried to modify game %', OLD.id;
    END IF;

    IF OLD.white_player <> NEW.black_player THEN
        RAISE EXCEPTION 'Modifying black_player in a service game is forbidden - tried to modify game %', OLD.id;
    END IF;

    IF OLD.service_id <> NEW.service_id THEN
        RAISE EXCEPTION 'Modifying service_id in a service game is forbidden - tried to modify game %', OLD.id;
    END IF;

    IF OLD.is_ranked <> NEW.is_ranked THEN
        RAISE EXCEPTION 'Modifying is_ranked in a service game is forbidden - tried to modify game %', OLD.id;
    END IF;

    RETURN NEW;
END;
$$;

CREATE OR REPLACE TRIGGER service_game_prevent_deletion
    BEFORE DELETE ON service_games
    FOR EACH ROW
EXECUTE FUNCTION prevent_game_deletion();

CREATE OR REPLACE TRIGGER service_game_prevent_updates
    BEFORE UPDATE ON service_games
    FOR EACH ROW
EXECUTE FUNCTION prevent_significant_game_changes();


CREATE OR REPLACE FUNCTION check_invalid_elo_history()
RETURNS trigger
LANGUAGE plpgsql
AS
$$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM games_rankings
        WHERE
            games_rankings.game_id = NEW.game_id AND
            games_rankings.ranking_id = NEW.ranking_id
    ) THEN
        RAISE EXCEPTION 'Elo history entry for game % and ranking % is not valid',
            games_rankings.game_id, games_rankings.ranking_id;
    END IF;

    RETURN NEW;
END;
$$;

CREATE OR REPLACE TRIGGER elo_history_prevent_invalid
    BEFORE INSERT OR UPDATE ON elo_history
    FOR EACH ROW
EXECUTE FUNCTION check_invalid_elo_history();

CREATE TABLE "swiss_tournaments"
(
    "tournament_id"     SERIAL              PRIMARY KEY,
    "round_count"       INTEGER             NOT NULL        CHECK ( round_count > 0 ), -- Swiss tournaments have a fixed round count
    "starting_position" VARCHAR             NOT NULL DEFAULT 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
    "is_ranked"         BOOLEAN             NOT NULL DEFAULT TRUE,
    "ranking_id"        INTEGER             NOT NULL        REFERENCES rankings(id),
    "time_control"      "clock_settings"    NOT NULL
);

CREATE TABLE "tournaments_games"
(
    "tournament_id"     INTEGER     NOT NULL    REFERENCES swiss_tournaments(tournament_id) ON DELETE CASCADE,
    "game_id"           INTEGER     NOT NULL    REFERENCES service_games(id),
    "round"             INTEGER     NOT NULL    CHECK ( round > 0 )
);

CREATE TABLE "tournaments_players"
(
    "service_id"        INTEGER     NOT NULL DEFAULT 1  REFERENCES game_services(id)
        CHECK (service_id = 1),
    "tournament_id"     INTEGER     NOT NULL    REFERENCES swiss_tournaments(tournament_id) ON DELETE CASCADE,
    "user_id_in_service" VARCHAR    NOT NULL,
    FOREIGN KEY ("service_id", "user_id_in_service")
        REFERENCES "service_accounts" ("service_id", "user_id_in_service"),
    UNIQUE ("tournament_id", "user_id_in_service")
);

CREATE TABLE "byes"
(
    "tournament_id"         INTEGER     NOT NULL            REFERENCES swiss_tournaments(tournament_id),
    "round"                 INTEGER     NOT NULL            CHECK ( round > 0 ),
    "user_id_in_service"    VARCHAR     NOT NULL,
    FOREIGN KEY ("tournament_id", "user_id_in_service")
        REFERENCES "tournaments_players" ("tournament_id", "user_id_in_service"),
    UNIQUE ("tournament_id", "round", "user_id_in_service")
);

-- Ranking value requirement placed on a tournament
CREATE TABLE "tournaments_ranking_reqs"
(
    "tournament_id"     INTEGER     NOT NULL    REFERENCES swiss_tournaments(tournament_id) ON DELETE CASCADE,
    "ranking_type"      INTEGER     NOT NULL    REFERENCES rankings(id) ON DELETE CASCADE,
    "required_value"    INTEGER     NOT NULL    CHECK ( required_value > 0 )
);

-- Number of rated games in certain ranking placed on a tournament
CREATE TABLE "tournaments_ranked_games_reqs"
(
    "tournament_id"     INTEGER     NOT NULL    REFERENCES swiss_tournaments(tournament_id) ON DELETE CASCADE,
    "ranking_type"      INTEGER     NOT NULL    REFERENCES rankings(id) DEFAULT 0, -- 0 is the global ranking
    "game_count"        INTEGER     NOT NULL    CHECK ( game_count > 0 ),
    UNIQUE(tournament_id, ranking_type)
);

CREATE VIEW "tournaments_reqs" AS
(
    SELECT tournament_id, ranking_type, game_count, null AS required_value
    FROM tournaments_ranked_games_reqs
    UNION ALL
    SELECT tournament_id, ranking_type, null AS game_count, required_value
    FROM tournaments_ranking_reqs
);

CREATE OR REPLACE FUNCTION calculate_performance_rating(opponent_elos NUMERIC[], myelo NUMERIC, points NUMERIC) RETURNS NUMERIC AS
$$
DECLARE
    lo NUMERIC := 0;
    hi NUMERIC := 5000;
    mid NUMERIC;
    expected NUMERIC;
    elo NUMERIC;
BEGIN
    IF(opponent_elos IS NULL) THEN RETURN 0; END IF;
    WHILE(hi - lo > 0.001) LOOP
        mid := (lo + hi)/2;
        expected := 0;
        FOREACH elo IN ARRAY opponent_elos LOOP
            expected := expected + (1 / (1 + 10^((elo - myelo)/400)));
        END LOOP;
        IF (expected < points) THEN
           lo := mid;
        ELSE
           hi := mid;
        END IF;
    END LOOP;
    RETURN lo;
END;
$$
LANGUAGE plpgsql;

CREATE VIEW "swiss_tournaments_players_points" AS
(
    WITH point_values AS (
        SELECT st.tournament_id, tp.user_id_in_service, tg.round,
        (
            SELECT COUNT(*)
            FROM tournaments_games tg2
            JOIN service_games sg ON(tg2.game_id = sg.id)
            WHERE sg.service_id = 1
                AND ((sg.white_player = tp.user_id_in_service AND (sg.result).game_end_type = '1-0')
                OR (sg.black_player = tp.user_id_in_service AND (sg.result).game_end_type = '0-1'))
                AND tg2.round <= tg.round
        )+(
            SELECT COUNT(*)
            FROM tournaments_games tg2
            JOIN service_games sg ON(tg2.game_id = sg.id)
            WHERE sg.service_id = 1
                AND (sg.white_player = tp.user_id_in_service OR sg.black_player = tp.user_id_in_service)
                AND (sg.result).game_end_type = '1/2-1/2'
                AND tg2.round <= tg.round
        )::numeric/2+(
            SELECT COUNT(*)
            FROM byes b
            WHERE b.tournament_id=st.tournament_id AND b.user_id_in_service=tp.user_id_in_service AND b.round <= tg.round
        ) AS points
        FROM swiss_tournaments st
        JOIN tournaments_players tp USING(tournament_id)
        JOIN tournaments_games tg USING(tournament_id)
        GROUP BY st.tournament_id, tp.user_id_in_service, tg.round
    )
    SELECT st.tournament_id, tp.user_id_in_service, tg.round, pv.points,
    calculate_performance_rating(
        -- TODO: Something is likely wrong in this subquery. Investigate using example data.
            (SELECT ARRAY_AGG(rat.elo)
            FROM current_ranking rat
                JOIN tournaments_games tg2 ON (tg2.tournament_id=st.tournament_id AND tg2.round <= tg.round)
                JOIN service_games sg ON (sg.id = tg2.game_id AND (sg.white_player = tp.user_id_in_service OR sg.black_player = tp.user_id_in_service))
                JOIN service_accounts opp ON (sg.white_player = opp.user_id_in_service OR sg.black_player = opp.user_id_in_service)
                    AND opp.user_id_in_service != tp.user_id_in_service
            WHERE rat.user_id_in_service=opp.user_id_in_service AND rat.ranking_id=st.ranking_id
        ), -- opponents' ratings
        (
            SELECT rat.elo
            FROM current_ranking rat
            WHERE rat.user_id_in_service=tp.user_id_in_service AND rat.ranking_id=st.ranking_id
        ), -- own rating
        pv.points
    ) AS performance_rating
    FROM swiss_tournaments st
        JOIN tournaments_players tp USING(tournament_id)
        JOIN tournaments_games tg USING(tournament_id)
        JOIN point_values pv ON(pv.tournament_id=st.tournament_id AND tp.user_id_in_service=pv.user_id_in_service AND tg.round=pv.round)
    GROUP BY st.tournament_id, tp.user_id_in_service, tg.round, pv.points
);

CREATE VIEW "swiss_tournaments_round_standings" AS
(
    SELECT ROW_NUMBER() OVER (PARTITION BY st.tournament_id, stpp.round ORDER BY stpp.points DESC, stpp.performance_rating DESC) place,
    stpp.points, stpp.performance_rating, stpp.user_id_in_service, st.tournament_id, stpp.round
    FROM swiss_tournaments st
        JOIN swiss_tournaments_players_points stpp on st.tournament_id = stpp.tournament_id
    ORDER BY place
);

-- Poniższy trigger sprawdza, czy time control turnieju jest zgodny z jego rating type
CREATE OR REPLACE FUNCTION check_tournament_validity()
    RETURNS TRIGGER AS
$$
DECLARE
BEGIN
    -- Check if tournament's time control is within the given rating
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

-- Poniższy trigger sprawdza wszelkie przypadki niepoprawnych wpisów w tournaments_games
CREATE OR REPLACE FUNCTION check_tournament_game_validity()
    RETURNS TRIGGER AS
$$
DECLARE
    tournament_data RECORD;
    game_data RECORD;
    white_player_id VARCHAR := NULL;
    black_player_id VARCHAR := NULL;
BEGIN
    SELECT *
    INTO tournament_data
    FROM swiss_tournaments st
    WHERE st.tournament_id = NEW.tournament_id;

    IF(tournament_data IS NULL) THEN RAISE EXCEPTION 'Invalid tournament'; END IF;
    IF(tournament_data.round_count < NEW.round) THEN RAISE EXCEPTION 'Round exceeds maximum defined by the tournament'; END IF;

    SELECT *
    INTO game_data
    FROM service_games sg
    WHERE sg.id = NEW.game_id AND sg.service_id = 1;

    IF(game_data IS NULL) THEN RAISE EXCEPTION 'Game not found in local games.'; END IF;
    IF(tournament_data.is_ranked != game_data.is_ranked) THEN RAISE EXCEPTION 'Game and tournament ranking mismatch'; END IF;
    IF(tournament_data.starting_position != game_data.starting_position) THEN
        RAISE EXCEPTION 'Game and tournament starting position mismatch. Tournament has % and game has %', tournament_data.starting_position, game_data.starting_position;
    END IF;
    IF((tournament_data.time_control)."starting_time" != (game_data.clock)."starting_time") THEN RAISE EXCEPTION 'Time control mismatch'; END IF;
    IF((tournament_data.time_control)."move_increase" != (game_data.clock)."move_increase") THEN RAISE EXCEPTION 'Time control increment mismatch'; END IF;
    white_player_id := (
        SELECT sa.user_id_in_service
        FROM service_accounts sa
        WHERE sa.user_id_in_service = game_data.white_player AND sa.service_id = 1
    );
    black_player_id := (
        SELECT sa.user_id_in_service
        FROM service_accounts sa
        WHERE sa.user_id_in_service = game_data.black_player AND sa.service_id = 1
    );
    IF(white_player_id IS NULL OR black_player_id IS NULL) THEN RAISE EXCEPTION 'Player not found among local accounts'; END IF;
    IF NOT EXISTS (
        SELECT tp.user_id_in_service
        FROM tournaments_players tp
        WHERE tp.user_id_in_service = white_player_id
    ) THEN RAISE EXCEPTION 'White player does not participate in the tournament.'; END IF;
    IF NOT EXISTS (
        SELECT tp.user_id_in_service
        FROM tournaments_players tp
        WHERE tp.user_id_in_service = black_player_id
    ) THEN RAISE EXCEPTION 'Black player does not participate in the tournament.'; END IF;
    RETURN NEW;
END;
$$
    LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER check_tournament_game_validity BEFORE INSERT OR UPDATE ON tournaments_games
    FOR EACH ROW EXECUTE PROCEDURE check_tournament_game_validity();

CREATE OR REPLACE FUNCTION check_tournament_player_validity()
    RETURNS TRIGGER AS
$$
DECLARE
    player_data RECORD;
    ranking_restriction RECORD;
    games_restriction RECORD;
    req_val INTEGER;
BEGIN
    SELECT *
    INTO player_data
    FROM service_accounts sa
    WHERE sa.user_id_in_service=NEW.user_id_in_service AND sa.service_id = 1
    LIMIT 1;
    IF(player_data IS NULL) THEN RAISE EXCEPTION 'Player not found in local service accounts.'; END IF;
    IF(player_data.is_bot) THEN RETURN NEW; END IF;

    -- Check all requirements for ranking minimum
    FOR ranking_restriction IN (SELECT * FROM tournaments_ranking_reqs trq WHERE NEW.tournament_id=trq.tournament_id) LOOP
        req_val = (
            SELECT rat.elo
            FROM current_ranking rat
            WHERE NEW.user_id_in_service=rat.user_id_in_service AND trq.ranking_type=rat.ranking_id
        );
        IF(req_val < trq.required_value) THEN RAISE EXCEPTION 'Could not join the tournament - rating too low.'; END IF;
    END LOOP;

    -- Check all requirements for ranked game minimum
    FOR games_restriction IN (SELECT * FROM tournaments_ranked_games_reqs trgr WHERE NEW.tournament_id=trgr.tournament_id) LOOP
        req_val = (
            SELECT COUNT(*)
            FROM elo_history eh
            WHERE eh.user_id_in_service=NEW.user_id_in_service AND eh.ranking_id=trgr.ranking_id
        );
        IF(req_val < trq.game_count) THEN RAISE EXCEPTION 'Could not join tournament - not enough ranked games.'; END IF;
    END LOOP;
    RETURN NEW;
END;
$$
    LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER check_tournament_player_validity BEFORE INSERT OR UPDATE ON tournaments_players
    FOR EACH ROW EXECUTE PROCEDURE check_tournament_player_validity();

INSERT INTO game_services(name) VALUES
    ('chess.com'),
    ('lichess.org');

UPDATE service_accounts SET display_name = 'Admin' WHERE user_id = 1;

INSERT INTO service_accounts("user_id", "service_id", "user_id_in_service", "is_bot", "display_name") VALUES
    (NULL, 1, 'stockfish-easy', TRUE, 'Stockfish (Easy)'),
    (NULL, 1, 'stockfish-medium', TRUE, 'Stockfish (Medium)'),
    (NULL, 1, 'stockfish-hard', TRUE, 'Stockfish (Hard)'),
    (NULL, 1, 'stockfish-impossible', TRUE, 'Stockfish (Impossible)');


INSERT INTO rankings("name", "playtime_min", "playtime_max", "extra_move_multiplier", "starting_elo", "include_bots", "k_factor") VALUES
    (
        'Global ranking',
        '0 seconds'::interval,
        NULL,
        0,
        800,
        TRUE,
        40
    ),
    (
        'Bullet',
        '0 seconds'::interval,
        '3 minutes'::interval,
        60,
        800,
        FALSE,
        40
    ),
    (
        'Bullet with bots',
        '0 seconds'::interval,
        '3 minutes'::interval,
        60,
        800,
        TRUE,
        40
    ),
    (
        'Blitz',
        '3 minutes'::interval,
        '10 minutes'::interval,
        60,
        800,
        FALSE,
        40
    ),
    (
        'Blitz with bots',
        '3 minutes'::interval,
        '10 minutes'::interval,
        60,
        800,
        TRUE,
        40
    ),
    (
        'Rapid',
        '10 minutes'::interval,
        '60 minutes'::interval,
        60,
        800,
        FALSE,
        40
    ),
    (
        'Rapid with bots',
        '10 minutes'::interval,
        '60 minutes'::interval,
        60,
        800,
        TRUE,
        40
    ),
    (
        'Classical',
        '1 hour'::interval,
        '24 hours'::interval,
        60,
        800,
        FALSE,
        40
    ),
    (
        'Classical with bots',
        '1 hour'::interval,
        '24 hours'::interval,
        60,
        800,
        TRUE,
        40
    );


