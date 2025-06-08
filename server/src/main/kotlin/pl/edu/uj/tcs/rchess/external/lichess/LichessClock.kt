package pl.edu.uj.tcs.rchess.external.lichess

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class LichessClock(
    val initial: Int,
    val increment: Int,
    val totalTime: Int
)