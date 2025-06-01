package pl.edu.uj.tcs.rchess.api.entity

enum class Service(val id: Int) {
    UNKNOWN(0),
    RANDOM_CHESS(1),
    CHESS_COM(2),
    LICHESS(3);

    companion object {
        fun fromId(id: Int): Service = entries.find { it.id == id } ?: UNKNOWN
    }
}
