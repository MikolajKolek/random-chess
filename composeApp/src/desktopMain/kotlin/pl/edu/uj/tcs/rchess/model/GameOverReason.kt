package pl.edu.uj.tcs.rchess.model

enum class GameOverReason {
    UNKNOWN,
    TIMEOUT,
    CHECKMATE,
    RESIGNATION,
    TIMEOUT_VS_INSUFFICIENT_MATERIAL,
    INSUFFICIENT_MATERIAL,
    THREEFOLD_REPETITION,
    FIFTY_MOVE_RULE,
    //TODO: if multiplayer games are ever added, this needs to be implemented
    //AGREEMENT,
    STALEMATE
}