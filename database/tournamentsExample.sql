-- Creating a few tournaments for our needs.
INSERT INTO swiss_tournaments(tournament_id, round_count, ranking_id, time_control, is_ranked) VALUES
    (
        10,
        5,
        1,
        '(3 minutes, 2 seconds)',
        true
    ),
    (
        20,
        5,
        1,
        '(1 minute, 0 seconds)',
        true
    ),
    (
        30,
        5,
        2,
        '(2 minutes, 1 second)',
        false
    ),
    (
        50,
        9,
        1,
        '(10 minutes, 0 second)',
        true
    ),
    (
        60,
        9,
        1,
        '(10 minutes, 0 second)',
        true
    ),
    (
        70,
        5,
        1,
        '(10 minutes, 0 second)',
        false
    );

-- Test mismatched time control
-- Should throw time control mismatch exception
INSERT INTO swiss_tournaments(tournament_id, round_count, ranking_id, time_control, is_ranked) VALUES
    (
        40,
        7,
        2,
        '(5 minutes, 3 seconds)',
        true
    );

-- Test adding game without player
INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10000,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(0-1,TIMEOUT)',
        NULL,
        '(3 minutes, 2 seconds)',
        TRUE,
        NULL,
        1,
        1,
        2
    ),
    (
        10001,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(0-1,TIMEOUT)',
        NULL,
        '(2 minutes, 1 second)',
        FALSE,
        NULL,
        1,
        1,
        2
    );

-- White player does not participate in the tournament
INSERT INTO tournaments_games(tournament_id, game_id, round) VALUES
    (10, 10000, 1);

-- White player does not participate in the tournament
INSERT INTO tournaments_games(tournament_id, game_id, round) VALUES
    (30, 10001, 3);

-- Add high required ranking to tournament 50.
INSERT INTO tournaments_ranking_reqs(tournament_id, ranking_type, required_value) VALUES
    (50, 1, 2000);

-- Add high required games to tournament 60.
INSERT INTO tournaments_ranked_games_reqs(tournament_id, ranking_type, game_count) VALUES
    (60, 1, 50);

-- Add players to all tournaments
INSERT INTO tournaments_players(tournament_id, user_id_in_service) VALUES
    (10, 11),
    (10, 12),
    (10, 13),
    (10, 14),
    (10, 15),
    (10, 16),
    (10, 17),
    (10, 18),
    (10, 19),
    (10, 20),
    (20, 11),
    (20, 12),
    (20, 13),
    (20, 14),
    (20, 15),
    (20, 16),
    (20, 17),
    (20, 18),
    (20, 19),
    (20, 20),
    (30, 11),
    (30, 12),
    (30, 13),
    (30, 14),
    (30, 15),
    (30, 16),
    (30, 17),
    (30, 18),
    (30, 19),
    (30, 20),
    (30, 21),
    (30, 22),
    (30, 23),
    (70, 11),
    (70, 12),
    (70, 13),
    (70, 14),
    (70, 15),
    (70, 16),
    (70, 17),
    (70, 18),
    (70, 19),
    (70, 20),
    (70, 21),
    (70, 22),
    (70, 23);

-- Inserts of low rated players to tournament with ranking requirement will fail.
INSERT INTO tournaments_players(tournament_id, user_id_in_service) VALUES
    (50, 11);

-- Inserts of players with not enough rated games to tournament with game number requirement will fail.
INSERT INTO tournaments_players(tournament_id, user_id_in_service) VALUES
    (60, 11);

-- Show that all correct players were added.
SELECT * FROM tournaments_players;

-- Cannot add games where only one player is in the tournament.
INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10002,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(0-1,TIMEOUT)',
        NULL,
        '(3 minutes, 2 seconds)',
        TRUE,
        NULL,
        1,
        11,
        3
    );

-- Should throw black player not in tournament.
INSERT INTO tournaments_games(tournament_id, game_id, round) VALUES
    (10, 10002, 3);

-- Now it's time to showcase proper tournaments.

-- Simple tournament
-- Players with difference of 2 always draw, otherwise player with lower id wins.

-- Round 1
/*
    23-22 0-1
    21-20 0-1
    19-18 0-1
    17-16 0-1
    15-14 0-1
    13-12 0-1
    11 bye
*/

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10101,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(0-1,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        23,
        22
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10102,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(0-1,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        21,
        20
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10103,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(0-1,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        19,
        18
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10104,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(0-1,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        17,
        16
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10105,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(0-1,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        15,
        14
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10106,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(0-1,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        13,
        12
    );

INSERT INTO byes(tournament_id, round, user_id_in_service) VALUES
    (70, 1, 11);

INSERT INTO tournaments_games(tournament_id, game_id, round) VALUES
    (70, 10101, 1),
    (70, 10102, 1),
    (70, 10103, 1),
    (70, 10104, 1),
    (70, 10105, 1),
    (70, 10106, 1);

-- Rankingi po rundzie 1
SELECT * FROM swiss_tournaments_round_standings WHERE tournament_id = 70 AND round = 1;

-- Round 2
/*
    22-20 1/2-1/2
    18-16 1/2-1/2
    14-12 1/2-1/2
    11-23 1-0
    21-19 1/2-1/2
    17-15 1/2-1/2
BYES: [13]
*/

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10201,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1/2-1/2,STALEMATE)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        22,
        20
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10202,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1/2-1/2,STALEMATE)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        18,
        16
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10203,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1/2-1/2,STALEMATE)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        14,
        12
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10204,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1-0,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        11,
        23
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10205,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1/2-1/2,STALEMATE)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        21,
        19
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10206,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1/2-1/2,STALEMATE)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        17,
        15
    );

INSERT INTO tournaments_games(tournament_id, game_id, round) VALUES
    (70, 10201, 2),
    (70, 10202, 2),
    (70, 10203, 2),
    (70, 10204, 2),
    (70, 10205, 2),
    (70, 10206, 2);

INSERT INTO byes(tournament_id, round, user_id_in_service) VALUES
    (70, 2, 13);

-- Wyniki po rundzie 2
SELECT * FROM swiss_tournaments_round_standings WHERE tournament_id = 70 AND round = 1;
SELECT * FROM swiss_tournaments_round_standings WHERE tournament_id = 70 AND round = 2;

-- Round 3
/*
    11-22
    20-18
    16-14
    13-21
    19-17
    15-12
    BYES: [23]
*/

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10301,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1-0,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        11,
        22
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10302,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1/2-1/2,STALEMATE)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        20,
        18
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10303,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1/2-1/2,STALEMATE)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        16,
        14
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10304,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1-0,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        13,
        21
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10305,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1/2-1/2,STALEMATE)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        19,
        17
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10306,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(0-1,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        15,
        12
    );

INSERT INTO tournaments_games(tournament_id, game_id, round) VALUES
    (70, 10301, 3),
    (70, 10302, 3),
    (70, 10303, 3),
    (70, 10304, 3),
    (70, 10305, 3),
    (70, 10306, 3);

INSERT INTO byes(tournament_id, round, user_id_in_service) VALUES
    (70, 3, 23);

-- Wyniki po rundzie 3
SELECT * FROM swiss_tournaments_round_standings WHERE tournament_id = 70 AND round = 1;
SELECT * FROM swiss_tournaments_round_standings WHERE tournament_id = 70 AND round = 2;
SELECT * FROM swiss_tournaments_round_standings WHERE tournament_id = 70 AND round = 3;

-- Round 4
/*
    11-12
    20-16
    14-13
    18-22
    23-19
    17-21
    BYES: [15]
*/

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10401,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1-0,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        11,
        12
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10402,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(0-1,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        20,
        16
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10403,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(0-1,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        14,
        13
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10404,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1-0,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        18,
        22
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10405,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(0-1,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        23,
        19
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10406,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1-0,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        17,
        21
    );

INSERT INTO tournaments_games(tournament_id, game_id, round) VALUES
    (70, 10401, 4),
    (70, 10402, 4),
    (70, 10403, 4),
    (70, 10404, 4),
    (70, 10405, 4),
    (70, 10406, 4);

INSERT INTO byes(tournament_id, round, user_id_in_service) VALUES
    (70, 4, 15);

-- Wyniki po rundzie 4
SELECT * FROM swiss_tournaments_round_standings WHERE tournament_id = 70 AND round = 1;
SELECT * FROM swiss_tournaments_round_standings WHERE tournament_id = 70 AND round = 2;
SELECT * FROM swiss_tournaments_round_standings WHERE tournament_id = 70 AND round = 3;
SELECT * FROM swiss_tournaments_round_standings WHERE tournament_id = 70 AND round = 4;

-- Round 5
/*
    11-18
    16-13
    12-20
    19-14
    17-22
    15-23
BYES: [21]
*/

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10501,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1-0,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        11,
        18
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10502,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(0-1,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        16,
        13
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10503,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1-0,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        12,
        20
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10504,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(0-1,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        19,
        14
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10505,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1-0,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        17,
        22
    );

INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10506,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1-0,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        15,
        23
    );

INSERT INTO tournaments_games(tournament_id, game_id, round) VALUES
    (70, 10501, 5),
    (70, 10502, 5),
    (70, 10503, 5),
    (70, 10504, 5),
    (70, 10505, 5),
    (70, 10506, 5);

INSERT INTO byes(tournament_id, round, user_id_in_service) VALUES
    (70, 5, 21);

-- Wyniki po rundzie 4
SELECT * FROM swiss_tournaments_round_standings WHERE tournament_id = 70 AND round = 1;
SELECT * FROM swiss_tournaments_round_standings WHERE tournament_id = 70 AND round = 2;
SELECT * FROM swiss_tournaments_round_standings WHERE tournament_id = 70 AND round = 3;
SELECT * FROM swiss_tournaments_round_standings WHERE tournament_id = 70 AND round = 4;
SELECT * FROM swiss_tournaments_round_standings WHERE tournament_id = 70 AND round = 5;


INSERT INTO service_games(id, moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        10601,
        '{}',
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
        CURRENT_TIMESTAMP,
        '(1-0,TIMEOUT)',
        NULL,
        '(10 minutes, 0 seconds)',
        FALSE,
        NULL,
        1,
        22,
        23
    );

-- Should throw round too large
INSERT INTO tournaments_games(tournament_id, game_id, round) VALUES
    (70, 10501, 6);