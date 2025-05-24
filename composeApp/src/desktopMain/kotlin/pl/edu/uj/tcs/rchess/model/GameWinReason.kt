package pl.edu.uj.tcs.rchess.model

enum class GameWinReason {
    UNKNOWN,
    TIMEOUT,
    CHECKMATE,
    RESIGNATION;

    fun toDbWinReason() = name

    companion object {
        fun fromDbString(string: String?) = entries.find { it.name == string }
            ?: throw IllegalArgumentException("Invalid db win reason")
    }
}
