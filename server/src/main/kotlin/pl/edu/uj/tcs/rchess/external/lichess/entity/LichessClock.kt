package pl.edu.uj.tcs.rchess.external.lichess.entity

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
internal data class LichessClock(
    val initial: Int,
    val increment: Int,
    val totalTime: Int
)