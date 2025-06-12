package pl.edu.uj.tcs.rchess.model

enum class GameDrawReason {
    UNKNOWN,
    TIMEOUT_VS_INSUFFICIENT_MATERIAL,
    INSUFFICIENT_MATERIAL,
    THREEFOLD_REPETITION,
    FIFTY_MOVE_RULE,
    // If multiplayer games are ever added, this needs to be implemented here and in the database
    //AGREEMENT,
    STALEMATE;

    companion object
}