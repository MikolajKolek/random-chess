package pl.edu.uj.tcs.rchess.model

enum class GameWinReason {
    UNKNOWN,
    TIMEOUT,
    CHECKMATE,
    RESIGNATION,
    ABANDONMENT,
    DEATH;

    companion object
}
