package pl.edu.uj.tcs.rchess.view.board

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import org.jetbrains.compose.resources.painterResource
import pl.edu.uj.tcs.rchess.model.BoardState
import pl.edu.uj.tcs.rchess.model.Move
import pl.edu.uj.tcs.rchess.model.PlayerColor
import pl.edu.uj.tcs.rchess.model.Square
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
                moveInProgress = MoveInProgress(
                    startSquare = square,
                    possibleMoves = state.getLegalMovesFor(square),
                    startPiece = fromPiece,
                )
                return
            }
        }

        val selectedMoves = moveInProgress?.possibleMoves?.filter { move ->
            move.to == square
        }
        // TODO: Handle promotion, there could be multiple moves to the same square
        selectedMoves?.firstOrNull()?.let { onMove(it) }
        moveInProgress = null
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
                        moveInProgressCopy != null && moveInProgressCopy.targetSquares.contains(square) ->
                            if (piece?.owner == moveInProgressCopy.startPiece.owner.opponent) SquareHighlight.Capture
                            else SquareHighlight.Move
                        else -> null
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .let {
                                when {
                                    highlight == SquareHighlight.Start -> it.background(Color.Yellow)
                                    highlight == SquareHighlight.Move -> it.background(Color.Green)
                                    highlight == SquareHighlight.Capture -> it.background(Color.Red.copy(alpha = 0.4f))
                                    square.isDark -> it.background(Color.LightGray)
                                    else -> it
                                }
                            }
                            .let {
                                if (moveAvailableForColor != null) {
                                    it.clickable(onClick = {
                                        onSquareClicked(square)
                                    })
                                } else {
                                    it
                                }
                            },
                    ) {
                        if (piece != null) {
                            Text(
                                piece.unicodeSymbol,
                                fontSize = pieceSize.value.sp * 0.8f,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }

                        if (highlight == SquareHighlight.Capture) {
                            Icon(
                                modifier = Modifier.align(Alignment.Center).fillMaxSize(),
                                painter = painterResource(Res.drawable.square_capture),
                                contentDescription = null,
                                tint = Color.Black,
                            )
                        }
                    }
                }
            }
        }
    }
}
