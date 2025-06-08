package pl.edu.uj.tcs.rchess.external.lichess

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(LichessPlayerSerializer::class)
sealed interface LichessPlayer

object LichessPlayerSerializer: KSerializer<LichessPlayer> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("pl.edu.uj.tcs.rchess.server", PrimitiveKind.STRING)

    @OptIn(InternalSerializationApi::class)
    override fun deserialize(decoder: Decoder): LichessPlayer {
        val input = decoder as? JsonDecoder ?: error("OpponentSerializer works with JSON")
        val json = input.decodeJsonElement().jsonObject

        return if ("aiLevel" in json) {
            val aiLevel = json["aiLevel"]!!.jsonPrimitive.int
            return LichessBot(aiLevel)
        } else if("user" in json) {
            input.json.decodeFromJsonElement(LichessAccount::class.serializer(), JsonObject(json))
        } else {
            EmptyLichessPlayer()
        }
    }

    @OptIn(InternalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: LichessPlayer) {
        val jsonEncoder = encoder as? JsonEncoder ?: error("OpponentSerializer works with JSON")
        val jsonElement: JsonElement = when (value) {
            is LichessBot -> buildJsonObject { put("aiLevel", value.aiLevel) }
            is LichessAccount -> jsonEncoder.json.encodeToJsonElement(LichessAccount::class.serializer(), value)
            is EmptyLichessPlayer -> buildJsonObject { }
        }
        jsonEncoder.encodeJsonElement(jsonElement)
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class LichessAccount(
    val user: LichessUser,
    val rating: Int
) : LichessPlayer

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class LichessUser(
    val name: String,
    val id: String,
    val title: String? = null
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class LichessBot(
    val aiLevel: Int
) : LichessPlayer

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
class EmptyLichessPlayer : LichessPlayer