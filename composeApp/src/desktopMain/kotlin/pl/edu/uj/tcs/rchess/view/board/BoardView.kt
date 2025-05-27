package pl.edu.uj.tcs.rchess.view.board

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.times
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square
import pl.edu.uj.tcs.rchess.model.state.BoardState
import pl.edu.uj.tcs.rchess.util.runIf
import pl.edu.uj.tcs.rchess.viewmodel.board.MoveInProgress
import rchess.composeapp.generated.resources.Res
import rchess.composeapp.generated.resources.square_capture

/**
 * Get the rank indexes as they appear from top to bottom on the screen
 * when playing as the player with color [orientation].
 */
fun ranksFor(orientation: PlayerColor) = when (orientation) {
    PlayerColor.WHITE -> 7 downTo 0
    PlayerColor.BLACK -> 0..7
}

/**
 * Get the file indexes as they appear from left to right on the screen
 * when playing as the player with color [orientation].
 */
fun filesFor(orientation: PlayerColor) = when (orientation) {
    PlayerColor.WHITE -> 0..7
    PlayerColor.BLACK -> 7 downTo 0
}

enum class SquareHighlight {
    Start,
    Capture,
    Move,
}

@Composable
fun BoardView(
    pieceSize: Dp,
    state: BoardState,
    orientation: PlayerColor,
    moveEnabledForColor: PlayerColor? = null,
    onMove: (move: Move) -> Unit = {},
) {
    var moveInProgress: MoveInProgress? by remember { mutableStateOf(null) }

    val moveAvailableForColor = moveEnabledForColor?.takeIf {
        it == state.currentTurn
    }

    // Cancel the move if it's no longer possible
    if (moveInProgress != null && moveAvailableForColor == null) {
        moveInProgress = null
    }

    fun onSquareClicked(square: Square) {
        state.board[square]?.let { fromPiece ->
            if (fromPiece.owner == moveAvailableForColor && square != moveInProgress?.startSquare) {
                moveInProgress = MoveInProgress.FirstPicked(
                    startSquare = square,
                    possibleMoves = state.getLegalMovesFor(square),
                    startPiece = fromPiece,
                )
                return
            }
        }

        moveInProgress = (moveInProgress as? MoveInProgress.FirstPicked)?.let { firstPicked ->
            val selectedMoves = firstPicked.possibleMoves.filter { move ->
                move.to == square
            }
            if (selectedMoves.size > 1) {
                MoveInProgress.Promotion(
                    startSquare = firstPicked.startSquare,
                    startPiece = firstPicked.startPiece,
                    promotionMoves = selectedMoves,
                )
            } else {
                selectedMoves.firstOrNull()?.let { onMove(it) }
                null
            }
        }
    }

    val moveInProgressCopy = moveInProgress
    Column(
        modifier = Modifier
            .width(8 * pieceSize)
            .height(8 * pieceSize)
            .aspectRatio(1f),
    ) {
        for (rank in ranksFor(orientation)) {
            Row(
                Modifier.fillMaxWidth().weight(1f),
            ) {
                for (file in filesFor(orientation)) {
                    val square = Square(rank = rank, file = file)
                    val piece = state.board[square]

                    val highlight: SquareHighlight? = when {
                        moveInProgressCopy?.startSquare == square -> SquareHighlight.Start
                        moveInProgressCopy is MoveInProgress.FirstPicked && moveInProgressCopy.targetSquares.contains(square) ->
                            if (piece?.owner == moveInProgressCopy.startPiece.owner.opponent) SquareHighlight.Capture
                            else SquareHighlight.Move
                        else -> null
                    }

                    val promotionPieces = (moveInProgressCopy as? MoveInProgress.Promotion)?.let { promotion ->
                        promotion.promotionMoves
                            .filter { it -> it.to == square }
                            .mapNotNull { move ->
                                move.promoteTo?.let { promoteTo ->
                                    move to promoteTo.toPiece(promotion.startPiece.owner)
                                }
                            }
                    } ?: emptyList()

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .run {
                                when {
                                    highlight == SquareHighlight.Start -> background(Color.Yellow)
                                    highlight == SquareHighlight.Move -> background(Color.Green)
                                    // TODO: Don't use transparent colors as board background
                                    highlight == SquareHighlight.Capture -> background(Color.Red.copy(alpha = 0.4f))
                                    square.isDark -> background(Color.LightGray)
                                    else -> background(Color.White)
                                }
                            }
                            .runIf(moveAvailableForColor != null && promotionPieces.isEmpty()) {
                                clickable(onClick = {
                                    onSquareClicked(square)
                                })
                            },
                    ) {
                        if (highlight == SquareHighlight.Capture) {
                            Icon(
                                modifier = Modifier.align(Alignment.Center).fillMaxSize(),
                                painter = painterResource(Res.drawable.square_capture),
                                contentDescription = null,
                                tint = Color.Black,
                            )
                        }

                        if (promotionPieces.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                promotionPieces.chunked(2).forEach { chunk ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().weight(1f),
                                    ) {
                                        chunk.forEach { (move, piece) ->
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .weight(1f)
                                                    .clickable {
                                                        onMove(move)
                                                        moveInProgress = null
                                                    },
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Image(
                                                    painter = painterResource(piece.icon),
                                                    contentDescription = piece.fenLetter.toString(),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (piece != null) {
                            Image(
                                modifier = Modifier.align(Alignment.Center),
                                painter = painterResource(piece.icon),
                                contentDescription = piece.fenLetter.toString(),
                            )
                        }
                    }
                }
            }
        }
    }
}
