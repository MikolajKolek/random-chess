package pl.edu.uj.tcs.rchess.tournament

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import kotlinx.coroutines.reactive.awaitFirst
import org.jooq.SQLDialect
import org.jooq.impl.DSL.using
import pl.edu.uj.tcs.rchess.config.ConfigLoader
import pl.edu.uj.tcs.rchess.generated.db.tables.references.SWISS_TOURNAMENTS
import pl.edu.uj.tcs.rchess.generated.db.udt.records.ClockSettingsTypeRecord

class TournamentFactory() {

    private val config = ConfigLoader.loadConfig()
    private val connection = ConnectionFactories.get(
        ConnectionFactoryOptions
            .parse("r2dbc:postgresql://${config.database.host}:${config.database.port}/${config.database.database}")
            .mutate()
            .option(ConnectionFactoryOptions.USER, config.database.user)
            .option(ConnectionFactoryOptions.PASSWORD, config.database.password)
            .build()
    )
    private val dsl = using(connection, SQLDialect.POSTGRES)

    suspend fun initializeTournament(
        round_count: Int,
        starting_position: String,
        is_ranked: Boolean,
        ranking_id: Int,
        time_control: ClockSettingsTypeRecord
    ) : Tournament {
        val id = dsl.insertInto(SWISS_TOURNAMENTS)
            .set(SWISS_TOURNAMENTS.ROUND_COUNT, round_count)
            .set(SWISS_TOURNAMENTS.STARTING_POSITION, starting_position)
            .set(SWISS_TOURNAMENTS.IS_RANKED, is_ranked)
            .set(SWISS_TOURNAMENTS.RANKING_ID, ranking_id)
            .set(SWISS_TOURNAMENTS.TIME_CONTROL, time_control)
            .returningResult(SWISS_TOURNAMENTS.TOURNAMENT_ID)
            .awaitFirst()?.getValue(SWISS_TOURNAMENTS.TOURNAMENT_ID)
            ?: throw IllegalStateException("Failed to save tournament to the database")
        return Tournament(id)
    }
}