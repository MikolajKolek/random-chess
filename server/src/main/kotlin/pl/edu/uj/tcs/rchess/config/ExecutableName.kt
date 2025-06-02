package pl.edu.uj.tcs.rchess.config

import com.sksamuel.hoplite.*
import com.sksamuel.hoplite.decoder.NonNullableLeafDecoder
import com.sksamuel.hoplite.fp.Validated
import java.nio.file.Path
import kotlin.io.path.absolute
import kotlin.reflect.KType

/**
 * Data class for storing a program name or executable file path.
 *
 * If a path is provided [Decoder] will convert it to an absolute path relative to the config file.
 */
@JvmInline
internal value class ExecutableName(
    val value: String,
) {
    internal class Decoder(private val baseDir: Path): NonNullableLeafDecoder<ExecutableName> {
        override fun safeLeafDecode(
            node: Node,
            type: KType,
            context: DecoderContext
        ): ConfigResult<ExecutableName> {
            if (node !is StringNode) {
                return Validated.Invalid(ConfigFailure.Generic("Expected string for ExecutableName"))
            }
            val path = if (node.value.contains('/')) {
                baseDir.resolve(node.value).absolute().normalize().toString()
            } else {
                node.value
            }
            return Validated.Valid(ExecutableName(path))
        }

        override fun supports(type: KType) =
            type.classifier == ExecutableName::class

    }
}
