INSERT INTO service_accounts("user_id", "service_id", "user_id_in_service", "is_bot", "display_name") VALUES
    (NULL, 2, 'test_ccom_id', FALSE, 'test_chesscom'),
    (NULL, 2, 'test2_ccom_id', FALSE, 'test2_chesscom'),
    (NULL, 3, 'test2_lc_id', FALSE, 'test2_lichess'),
    (NULL, 2, 'makaron_ccom_id', FALSE, 'makaron_w_domu'),
    (NULL, 3, 'makaron_lc_id', FALSE, 'Djammnick'),
    (NULL, 2, 'chess_com_user', FALSE, 'chess_com_user_not_in_service'),
    (NULL, 3, 'lichess_user', FALSE, 'lichess_user_not_in_service'),
    (NULL, 2, 'chess_com_bot', TRUE, 'chess_com_bot'),
    (NULL, 1, 'internal_bot', TRUE, 'bot_1');

INSERT INTO users(email, password_hash) VALUES
    ('test1@randomchess.com', 'password1'),
    ('test2@randomchess.com', 'password2'),
    ('test3@randomchess.com', 'password3'),
    ('test4@randomchess.com', 'password4'),
    ('test5@randomchess.com', 'password5'),
    ('test6@randomchess.com', 'password6'),
    ('test7@randomchess.com', 'password7'),
    ('test8@randomchess.com', 'password8'),
    ('test9@randomchess.com', 'password9'),
    ('test10@randomchess.com', 'password10'),
    ('test11@randomchess.com', 'password11'),
    ('test12@randomchess.com', 'password12'),
    ('test13@randomchess.com', 'password13'),
    ('test14@randomchess.com', 'password14'),
    ('test15@randomchess.com', 'password15'),
    ('test16@randomchess.com', 'password16'),
    ('test17@randomchess.com', 'password17'),
    ('test18@randomchess.com', 'password18'),
    ('test19@randomchess.com', 'password19'),
    ('test20@randomchess.com', 'password20'),
    ('test21@randomchess.com', 'password21'),
    ('test22@randomchess.com', 'password22'),
    ('test23@randomchess.com', 'password23'),
    ('test24@randomchess.com', 'password24'),
    ('test25@randomchess.com', 'password25'),
    ('test26@randomchess.com', 'password26'),
    ('test27@randomchess.com', 'password27'),
    ('test28@randomchess.com', 'password28'),
    ('test29@randomchess.com', 'password29'),
    ('test30@randomchess.com', 'password30'),
    ('test31@randomchess.com', 'password31'),
    ('test32@randomchess.com', 'password32'),
    ('test33@randomchess.com', 'password33'),
    ('test34@randomchess.com', 'password34'),
    ('test35@randomchess.com', 'password35'),
    ('test36@randomchess.com', 'password36'),
    ('test37@randomchess.com', 'password37'),
    ('test38@randomchess.com', 'password38'),
    ('test39@randomchess.com', 'password39'),
    ('test40@randomchess.com', 'password40'),
    ('test41@randomchess.com', 'password41'),
    ('test42@randomchess.com', 'password42'),
    ('test43@randomchess.com', 'password43'),
    ('test44@randomchess.com', 'password44'),
    ('test45@randomchess.com', 'password45'),
    ('test46@randomchess.com', 'password46'),
    ('test47@randomchess.com', 'password47'),
    ('test48@randomchess.com', 'password48'),
    ('test49@randomchess.com', 'password49'),
    ('test50@randomchess.com', 'password50'),
    ('test51@randomchess.com', 'password51'),
    ('test52@randomchess.com', 'password52'),
    ('test53@randomchess.com', 'password53'),
    ('test54@randomchess.com', 'password54'),
    ('test55@randomchess.com', 'password55'),
    ('test56@randomchess.com', 'password56'),
    ('test57@randomchess.com', 'password57'),
    ('test58@randomchess.com', 'password58'),
    ('test59@randomchess.com', 'password59'),
    ('test60@randomchess.com', 'password60'),
    ('test61@randomchess.com', 'password61'),
    ('test62@randomchess.com', 'password62'),
    ('test63@randomchess.com', 'password63'),
    ('test64@randomchess.com', 'password64'),
    ('test65@randomchess.com', 'password65'),
    ('test66@randomchess.com', 'password66'),
    ('test67@randomchess.com', 'password67'),
    ('test68@randomchess.com', 'password68'),
    ('test69@randomchess.com', 'password69'),
    ('test70@randomchess.com', 'password70'),
    ('test71@randomchess.com', 'password71'),
    ('test72@randomchess.com', 'password72'),
    ('test73@randomchess.com', 'password73'),
    ('test74@randomchess.com', 'password74'),
    ('test75@randomchess.com', 'password75'),
    ('test76@randomchess.com', 'password76'),
    ('test77@randomchess.com', 'password77'),
    ('test78@randomchess.com', 'password78'),
    ('test79@randomchess.com', 'password79'),
    ('test80@randomchess.com', 'password80'),
    ('test81@randomchess.com', 'password81'),
    ('test82@randomchess.com', 'password82'),
    ('test83@randomchess.com', 'password83'),
    ('test84@randomchess.com', 'password84'),
    ('test85@randomchess.com', 'password85'),
    ('test86@randomchess.com', 'password86'),
    ('test87@randomchess.com', 'password87'),
    ('test88@randomchess.com', 'password88'),
    ('test89@randomchess.com', 'password89'),
    ('test90@randomchess.com', 'password90'),
    ('test91@randomchess.com', 'password91'),
    ('test92@randomchess.com', 'password92'),
    ('test93@randomchess.com', 'password93'),
    ('test94@randomchess.com', 'password94'),
    ('test95@randomchess.com', 'password95'),
    ('test96@randomchess.com', 'password96'),
    ('test97@randomchess.com', 'password97'),
    ('test98@randomchess.com', 'password98'),
    ('test99@randomchess.com', 'password99'),
    ('test100@randomchess.com', 'password100');

-- Przykładowe lokalne partie
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

-- Gambit name length stress-test
-- Queen's Pawn Game: Accelerated London System, Steinitz Countergambit, Morris Countergambit Accepted
INSERT INTO service_games(moves, starting_position, creation_date, result, metadata, clock, is_ranked, game_id_in_service, service_id, white_player, black_player) VALUES
    (
        '{}',
        'rnbqkbnr/pp2pppp/8/2p5/3PpB2/8/PPP2PPP/RN1QKBNR w KQkq - 0 1',
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

-- Games for the tournament tests
INSERT INTO swiss_tournaments(round_count, ranking_id, time_control) VALUES
    (
        5,
        1,
        '(3 minutes, 2 second)'
    );

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
    );
INSERT INTO service_games("moves", "creation_date", "result", "metadata", "service_id", "game_id_in_service", white_player, black_player) VALUES
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