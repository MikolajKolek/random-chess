package pl.edu.uj.tcs.rchess.external.lichess

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.time.Instant
import kotlin.time.toJavaInstant

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class LichessGamesResponse(
    val id: String,
    @Serializable(OffsetDateTimeAsLongSerializer::class)
    val createdAt: OffsetDateTime,
    @Serializable(OffsetDateTimeAsLongSerializer::class)
    val lastMoveAt: OffsetDateTime,
    val status: String,
    val players: Map<String, LichessPlayer>,
    val winner: String? = null,
    val pgn: String,
    val clock: LichessClock? = null
)

object OffsetDateTimeAsLongSerializer: KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("pl.edu.uj.tcs.rchess.server", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeLong(value.toInstant().toEpochMilli())
    }

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return OffsetDateTime.ofInstant(
            Instant.fromEpochMilliseconds(decoder.decodeLong()).toJavaInstant(),
            ZoneOffset.UTC
        )
    }
}