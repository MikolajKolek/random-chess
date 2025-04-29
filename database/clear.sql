DROP VIEW IF EXISTS game_openings;
DROP VIEW IF EXISTS games;
DROP TABLE IF EXISTS users, service_games, service_accounts, game_services, pgn_games, openings;
DROP FUNCTION IF EXISTS add_default_service_to_user(), prevent_default_service_modification(), prevent_default_service_deletion();
