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
        IF(to_square = en_passant) THEN
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

    -- On first move add en passant square
    en_passant := '-';
    IF(LOWER(piece) = 'p') THEN
        IF(substr(move, 2, 1) = '2' AND substr(move, 4, 1) = '4') THEN
            en_passant := substr(move, 1, 1)||'3';
        END IF;
        IF(substr(move, 2, 1) = '7' AND substr(move, 4, 1) = '5') THEN
            en_passant := substr(move, 1, 1)||'6';
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
    "creation_date"      TIMESTAMP          NOT NULL, -- data rozegrania partii
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
    "creation_date"     TIMESTAMP       NOT NULL, -- data zaimportowania partii
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
    SELECT "id", 'service' AS "kind", "starting_position", "moves", "partial_fens", "creation_date", "result", "metadata", "clock" FROM service_games
    UNION ALL
    SELECT "id", 'pgn' AS "kind", "starting_position", "moves", "partial_fens", "creation_date", "result", "metadata", "clock" FROM pgn_games
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
) RETURNS INTEGER AS
$$
DECLARE
    my_partial_fen VARCHAR;
    opening_id INTEGER := NULL;
BEGIN
    FOREACH my_partial_fen IN ARRAY partial_fens LOOP
        IF EXISTS(
            SELECT *
            FROM openings o
            WHERE o.partial_fen=my_partial_fen
        ) THEN
            opening_id := (SELECT o.id FROM openings o WHERE o.partial_fen=my_partial_fen LIMIT 1);
        END IF;
    END LOOP;
    RETURN opening_id;
END;
$$
LANGUAGE plpgsql;

CREATE VIEW games_openings AS (
    SELECT g.id AS game_id, CASE
        WHEN (dop IS NULL) THEN NULL
        ELSE dop
    END AS opening_id
    FROM games g, LATERAL detect_opening(partial_fens) dop
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
    "service_id"            INT         NOT NULL    REFERENCES "game_services" ("id")
        CHECK ("service_id" = 1),
    "user_id_in_service"    VARCHAR     NOT NULL,
    "ranking_id"            INT         NOT NULL    REFERENCES "rankings" ("id"),
    "game_id"               INT         NOT NULL    REFERENCES "service_games" ("id"),
    "elo"                   NUMERIC     NOT NULL,

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

CREATE VIEW current_ranking AS(
    SELECT
        DISTINCT ON (sa.service_id, sa.user_id_in_service, r.id)
        sa.service_id,
        sa.user_id_in_service,
        r.id AS "ranking_id",
        COALESCE(eh.elo, r.starting_elo) AS "elo"
    FROM service_accounts sa
    CROSS JOIN rankings r
    LEFT JOIN elo_history eh ON (r.id = eh.ranking_id AND sa.user_id_in_service = eh.user_id_in_service)
    LEFT JOIN service_games sg ON (eh.game_id = sg.id)
    WHERE (sa.service_id = 1) AND (r.include_bots OR NOT sa.is_bot)
    ORDER BY sa.service_id, sa.user_id_in_service, r.id, sg.creation_date DESC
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

    SELECT cr_black.elo, cr_white.elo, (sg.result).game_end_type
    INTO current_black_elo, current_white_elo, game_end_type
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

    INSERT INTO elo_history(service_id, user_id_in_service, ranking_id, game_id, elo) VALUES
        (
             1,
             (SELECT black_player FROM service_games WHERE id = service_game_id),
             ranking_id_to_update,
             service_game_id,
             current_black_elo + k_factor_var * (black_score - expected_black_value)
        ),
        (
            1,
            (SELECT white_player FROM service_games WHERE id = service_game_id),
            ranking_id_to_update,
            service_game_id,
            current_white_elo + k_factor_var * (white_score - expected_white_value)
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
    "tournament_id"     INTEGER     NOT NULL    REFERENCES swiss_tournaments(tournament_id) ON DELETE CASCADE,
    "user_id"           INTEGER     NULL        --REFERENCES service_accounts(user_id_in_service) ON DELETE SET NULL -- Is this correct? Likely done via trigger.
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
    "game_count"        INTEGER     NOT NULL    CHECK ( game_count > 0 )
);

CREATE VIEW "tournaments_reqs" AS
(
    SELECT tournament_id, ranking_type, game_count, null AS required_value
    FROM tournaments_ranked_games_reqs
    UNION ALL
    SELECT tournament_id, ranking_type, null AS game_count, required_value
    FROM tournaments_ranking_reqs
);

CREATE VIEW "swiss_tournaments_players_points" AS
(
    SELECT st.tournament_id, tp.user_id, tg.round, 0 AS points, 0 AS performance_rating -- TODO: Calculate points and performance rating given using tournament games for each round.
    FROM swiss_tournaments st
         JOIN tournaments_players tp USING(tournament_id)
         JOIN tournaments_games tg USING(tournament_id)
    GROUP BY st.tournament_id, tp.user_id, tg.round
);

CREATE VIEW "swiss_tournaments_round_standings" AS
(
    SELECT st.tournament_id, tg.round
    FROM swiss_tournaments st
         JOIN tournaments_players tp USING(tournament_id)
         JOIN tournaments_games tg USING(tournament_id)
         JOIN swiss_tournaments_players_points stpp on st.tournament_id = stpp.tournament_id AND stpp.round = tg.round
    ORDER BY stpp.points, stpp.performance_rating
);

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
    tournament_data := (
        SELECT *
        FROM swiss_tournaments st
        WHERE st.tournament_id = NEW.tournament_id
    );
    game_data := (
        SELECT *
        FROM service_games sg
        WHERE sg.id = NEW.game_id AND sg.service_id = 1
    );
    IF(game_data IS NULL) THEN RAISE EXCEPTION 'Game not found in local games.'; END IF;
    IF(tournament_data IS NULL) THEN RAISE EXCEPTION 'Invalid tournament'; END IF;
    IF(tournament_data.round_count < NEW.round) THEN RAISE EXCEPTION 'Round exceeds maximum defined by the tournament'; END IF;
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
        SELECT user_id
        FROM tournaments_players tp
        WHERE tp.user_id = white_player_id
    ) THEN RAISE EXCEPTION 'White player does not participate in the tournament.'; END IF;
    IF NOT EXISTS (
        SELECT user_id
        FROM tournaments_players tp
        WHERE tp.user_id = black_player_id
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
BEGIN
    player_data := (
        SELECT *
        FROM service_accounts sa
        WHERE sa.user_id_in_service=NEW.user_id AND sa.service_id = 1
    );
    IF(player_data IS NULL) THEN RAISE EXCEPTION 'Player not found in local service accounts.'; END IF;
    -- TODO: Verify that the player fulfills all requirements placed on the tournaments.
    RETURN NEW;
END;
$$
    LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER check_tournament_player_validity BEFORE INSERT OR UPDATE ON tournaments_players
    FOR EACH ROW EXECUTE PROCEDURE check_tournament_player_validity();

-- Przykładowe dane:
INSERT INTO game_services(name) VALUES
    ('chess.com'),
    ('lichess.org');

INSERT INTO users(email, password_hash) VALUES
    ('test@randomchess.com', 'empty'),
    ('test2@randomchess.com', 'empty2');;
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

INSERT INTO service_games(moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        '{e2e4,d7d6,g1f3,g8f6,b1c3,g7g6,d2d4,f8g7,a1b1,e8g8,h2h3,c7c5,d4d5,d8a5,c1d2,f6d7,c3b5,a5a2,b5c7,g7b2,c7a8,b8a6,f1d3,a6b4,d2b4,c5b4,a8c7,a7a6,e1f1,b2c3,f1e2,a2a5,c7e6,f7e6,d5e6,d7c5,b1a1,c3a1,d1a1,a5a1,h1a1,c8e6,f3d4,e6d7,a1b1,a6a5,d4b3,a5a4,b3c5,d6c5,d3c4,g8g7,e2e3,g7f6,f2f3,f6e5,f3f4,f8f4,e3e2,f4e4,e2f3,e4c4,b1e1,e5f6,e1f1,c4c2,f3e4,d7f5}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(0-1,TIMEOUT)',
        NULL,
        '(1 minute, 1 second)',
        TRUE,
        NULL,
        1,
        1,
        2
    );

INSERT INTO service_games(moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(0-1,TIMEOUT)',
        NULL,
        '(1 minute, 1 second)',
        TRUE,
        NULL,
        1,
        1,
        'stockfish-impossible'
    );

/*INSERT INTO users(email, password_hash) VALUES
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
    (NULL, 1, 'internal_bot', TRUE, 'bot_1');*/

/* New example game to be added
INSERT INTO service_games("moves", "starting_position", "creation_date", "result", "metadata", "service_id", "game_id_in_service", white_player, black_player) VALUES
    (
        ARRAY['e2e4', 'e5e7'],
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        '2025-04-24T16:02:54Z',
        'black_won',
        '{"TimeControl": "30+3"}',
        3,
        'zGsFNtCE',
        'test2_lc_id',
        'lichess_user'
    );*/

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

/*INSERT INTO openings("eco", "name", "partial_fen") VALUES
    ('D11','Slav Defense: Modern Line','rnbqkbnr/pp2pppp/2p5/3p4/2PP4/5N2/PP2PPPP/RNBQKB1R b KQkq -'), -- np. 1. d4 d5 2. c4 c6 3. Nf3
    ('B00','King''s Pawn Game','rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq -'), -- np. 1. e4
    ('A00','Polish Opening: Symmetrical Variation','rnbqkbnr/p1pppppp/8/1p6/1P6/8/P1PPPPPP/RNBQKBNR w KQkq -'), -- np. 1. b4 b5
    ('C57','Italian Game: Two Knights Defense, Knight Attack','r1bqkb1r/pppp1ppp/2n2n2/4p1N1/2B1P3/8/PPPP1PPP/RNBQK2R b KQkq -'), -- np. 1. e4 e5 2. Nf3 Nc6 3. Bc4 Nf6 4. Ng5 Bc5
    ('E01','Catalan Opening: Closed','rnbqkb1r/ppp2ppp/4pn2/3p4/2PP4/6P1/PP2PPBP/RNBQK1NR b KQkq -'), -- np. 1. d4 Nf6 2. c4 e6 3. g3 d5 4. Bg2
    ('C37','King''s Gambit Accepted: Muzio Gambit, Wild Muzio Gambit','rnbqkbnr/pppp1p1p/8/8/2B1Ppp1/5N2/PPPP2PP/RNBQ1RK1 b kq -'), -- np. 1. e4 e5 2. f4 exf4 3. Nf3 g5 4. Bc4 g4 5. O-O
    ('C44','Scotch Game: Scotch Gambit, Dubois Réti Defense','r1bqkb1r/pppp1ppp/2n2n2/8/2BpP3/5N2/PPP2PPP/RNBQK2R w KQkq -'), -- np. 1. e4 e5 2. Nf3 Nc6 3. d4 exd4 4. Bc4 Nf6
    ('E68','King''s Indian Defense: Fianchetto Variation, Classical Variation','r1bq1rk1/pppn1pbp/3p1np1/4p3/2PPP3/2N2NP1/PP3PBP/R1BQ1RK1 b - -'); -- np. 1. d4 Nf6 2. c4 g6 3. Nc3 Bg7 4. Nf3 d6 5. g3 O-O 6. Bg2 Nbd7 7. O-O e5 8. e4
-- Dodanie 20 wariantów pierwszego ruchu do bazy zapewni, że każda niepusta partia będzie mieć jakiś przypisany debiut
*/
