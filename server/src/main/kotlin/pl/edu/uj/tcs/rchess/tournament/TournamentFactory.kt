package pl.edu.uj.tcs.rchess.tournament

import pl.edu.uj.tcs.rchess.generated.db.udt.records.ClockSettingsTypeRecord
import pl.edu.uj.tcs.rchess.server.Database

internal class TournamentFactory(
    val database: Database
) {
    suspend fun initializeTournament(
        roundCount: Int,
        startingPosition: String,
        isRanked: Boolean,
        rankingId: Int,
        timeControl: ClockSettingsTypeRecord
    ) : Tournament {
        return Tournament(
            tournamentId = database.initializeTournament(
                roundCount = roundCount,
                startingPosition = startingPosition,
                isRanked = isRanked,
                rankingId = rankingId,
                timeControl = timeControl
            ),
            database = database
        )
    }
}