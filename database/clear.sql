DROP VIEW IF EXISTS games_openings, games, users_games;
DROP TABLE IF EXISTS users, service_games, service_accounts, game_services, pgn_games, openings;
DROP FUNCTION IF EXISTS add_default_service_to_user(), prevent_default_service_modification(), prevent_default_service_deletion();
