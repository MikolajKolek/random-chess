package pl.edu.uj.tcs.rchess.api.entity

interface PlayerDetails {
    val displayName: String

    data class Simple(
        override val displayName: String,
    ) : PlayerDetails
}
