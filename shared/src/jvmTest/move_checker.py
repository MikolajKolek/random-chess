import chess.pgn
import json
import sys

def get_legal_moves(board):
    # Convert legal moves to UCI notation strings
    return [move.uci() for move in board.legal_moves]

def process_pgn_database(pgn_file_path):
    pgn = open(pgn_file_path)

    while True:
        game = chess.pgn.read_game(pgn)
        if game is None:
            break

        # Get headers and remove player/result info for separate fields
        headers = dict(game.headers)
        white = headers.pop('White', None)
        black = headers.pop('Black', None)
        result = headers.pop('Result', None)

        # Get all positions FENs and their legal moves
        board = game.board()
        positions = [get_legal_moves(board)]

        for move in game.mainline_moves():
            board.push(move)
            positions.append(get_legal_moves(board))

        # Print as single-line JSON
        print(json.dumps(positions, separators=(',', ':')))

    pgn.close()

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <pgn_file_path>")
        sys.exit(1)

    pgn_file_path = sys.argv[1]
    process_pgn_database(pgn_file_path)