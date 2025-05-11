import chess.pgn
import json
import sys

def oldstyle_fen(board):
    builder = []
    builder.append(board.board_fen())
    builder.append("w" if board.turn == chess.WHITE else "b")
    builder.append(board.castling_xfen())
    builder.append(chess.SQUARE_NAMES[board.ep_square] if board.ep_square else "-")
    builder.append(str(board.halfmove_clock))
    builder.append(str(board.fullmove_number))
    return " ".join(builder)

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
        
        # Get all positions FENs
        board = game.board()
        fens = [oldstyle_fen(board)]
        
        for move in game.mainline_moves():
            board.push(move)
            fens.append(oldstyle_fen(board))
        
        game_data = {
            'fens': fens,
            'headers': headers,
            'white': white,
            'black': black,
            'result': result
        }
        
        # Print as single-line JSON
        print(json.dumps(game_data, separators=(',', ':')))
    
    pgn.close()

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <pgn_file_path>")
        sys.exit(1)
        
    pgn_file_path = sys.argv[1]
    process_pgn_database(pgn_file_path)