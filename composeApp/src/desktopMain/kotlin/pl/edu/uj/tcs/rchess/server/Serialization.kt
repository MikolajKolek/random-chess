package pl.edu.uj.tcs.rchess.server

import org.jooq.types.YearToSecond
import pl.edu.uj.tcs.rchess.generated.db.udt.records.ClockSettingsTypeRecord
import pl.edu.uj.tcs.rchess.model.ClockSettings
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

object Serialization {
    fun ClockSettings?.toDbRecord(): ClockSettingsTypeRecord {
        if (this == null) return ClockSettingsTypeRecord(
            startingTime = null,
            moveIncrease = null,
        )

        return ClockSettingsTypeRecord(
            startingTime = YearToSecond.valueOf(startingTime.toJavaDuration()),
            moveIncrease = YearToSecond.valueOf(moveIncrease.toJavaDuration()),
        )
    }

    fun ClockSettingsTypeRecord.toModel(): ClockSettings? {
        require((this.startingTime == null) == (this.moveIncrease == null)) {
            "Both startingTime and moveIncrease must be null or non-null"
        }
        val startingTime = this.startingTime ?: return null
        val moveIncrease = this.moveIncrease ?: return null

        return ClockSettings(
            startingTime = startingTime.toDuration().toKotlinDuration(),
            moveIncrease = moveIncrease.toDuration().toKotlinDuration(),
        )
    }

}
