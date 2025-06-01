package pl.edu.uj.tcs.rchess.server

import org.jooq.types.YearToSecond
import pl.edu.uj.tcs.rchess.api.entity.Ranking
import pl.edu.uj.tcs.rchess.generated.db.tables.records.RankingsRecord
import pl.edu.uj.tcs.rchess.generated.db.udt.records.ClockSettingsTypeRecord
import pl.edu.uj.tcs.rchess.model.ClockSettings
import kotlin.time.Duration
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

object Serialization {
    fun Duration.toDbInterval(): YearToSecond =
        YearToSecond.valueOf(this.toJavaDuration())

    fun YearToSecond.toKotlinDuration(): Duration =
        this.toDuration().toKotlinDuration()

    fun ClockSettings?.toDbType(): ClockSettingsTypeRecord {
        if (this == null) return ClockSettingsTypeRecord(
            startingTime = null,
            moveIncrease = null,
        )

        return ClockSettingsTypeRecord(
            startingTime = startingTime.toDbInterval(),
            moveIncrease = moveIncrease.toDbInterval(),
        )
    }

    fun ClockSettingsTypeRecord.toModel(): ClockSettings? {
        require((this.startingTime == null) == (this.moveIncrease == null)) {
            "Both startingTime and moveIncrease must be null or non-null"
        }
        val startingTime = this.startingTime ?: return null
        val moveIncrease = this.moveIncrease ?: return null

        return ClockSettings(
            startingTime = startingTime.toKotlinDuration(),
            moveIncrease = moveIncrease.toKotlinDuration(),
        )
    }

    fun RankingsRecord.toModel() = Ranking(
        id = id!!,
        name = name,
        playtimeMin = playtimeMin.toKotlinDuration(),
        playtimeMax = playtimeMax?.toKotlinDuration(),
        extraMoveMultiplier = extraMoveMultiplier,
        includeBots = includeBots
    )
}
