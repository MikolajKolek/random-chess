-- Poniższy trigger sprawdza wszelkie przypadki niepoprawnych wpisów w tournaments_games
-- Poprawiona weryfikacja turniejów
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
        WHERE tp.user_id_in_service = white_player_id AND tp.tournament_id = NEW.tournament_id
    ) THEN RAISE EXCEPTION 'White player does not participate in the tournament.'; END IF;
    IF NOT EXISTS (
        SELECT tp.user_id_in_service
        FROM tournaments_players tp
        WHERE tp.user_id_in_service = black_player_id AND tp.tournament_id = NEW.tournament_id
    ) THEN RAISE EXCEPTION 'Black player does not participate in the tournament.'; END IF;
    RETURN NEW;
END;
$$
    LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER check_tournament_game_validity BEFORE INSERT OR UPDATE ON tournaments_games
    FOR EACH ROW EXECUTE PROCEDURE check_tournament_game_validity();

ALTER TABLE swiss_tournaments
    DROP CONSTRAINT swiss_tournaments_check;

UPDATE swiss_tournaments
SET is_ranked = true
WHERE is_ranked IS NULL;

ALTER TABLE swiss_tournaments
    ALTER COLUMN is_ranked SET NOT NULL;