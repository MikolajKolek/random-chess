package pl.edu.uj.tcs.rchess.model

enum class GameDrawReason {
    UNKNOWN,
    TIMEOUT_VS_INSUFFICIENT_MATERIAL,
    INSUFFICIENT_MATERIAL,
    THREEFOLD_REPETITION,
    FIFTY_MOVE_RULE,
    //TODO: if multiplayer games are ever added, this needs to be implemented
    //AGREEMENT,
    STALEMATE;

    fun toDbWinReason() = name

    companion object {
        fun fromDbString(string: String?) = GameDrawReason.entries.find { it.name == string }
            ?: throw IllegalArgumentException("Invalid db draw reason")
    }
}