# Random Chess
The ***Random Chess*** project intends to help organize chess training by aggregating games from various sources with the ability to play games offline with bots. 

Implemented functionality includes:
- Playing games locally with bots
  - Move legality verification
  - Integration with existing chess engines - currently [Stockfish](https://stockfishchess.org/) is the default option, but most modern chess engines can be used thanks to the implementation of the [UCI Protocol](https://backscattering.de/chess/uci/)
- Game history, including:
  - Games imported in the [Portable Game Notation](https://pl.wikipedia.org/wiki/Portable_Game_Notation) format
  - Games automatically downloaded from connected accounts on [lichess.org](https://lichess.org/api)
  - Games played in our service
- The ability to export history games in PGN format
- Game analysis:
  - Opening detection
  - Displaying moves in [algebraic notation](https://en.wikipedia.org/wiki/Algebraic_notation_(chess))
- Estimating [ELO](https://en.wikipedia.org/wiki/Elo_rating_system) rankings based on locally played games, separately for different time control categories
- Conducting tournaments in the [Swiss tournament system](https://en.wikipedia.org/wiki/Swiss-system_tournament) (only the database part is functional, although the application has a module for matchings)
- Maintaining a level of separation between the client and server modules, with communication being limited to the `ClientApi` interface, which allows for easy extension to a full client-server application


# Getting started
Copy the config file from [local/config.example.yml](local/config.example.yml) to:
- on Linux: `~/.local/share/rchess/config.yml`
- on Windows: `%APPDATA%/rchess/config.yml`
- on macOS: `~/Library/Application Support/rchess/config.yml`

Opening detection requires data from the openings database.
Clone [the Lichess openings database](https://github.com/lichess-org/chess-openings).
Run `make` there after downloading the [chess](https://pypi.org/project/chess/) python package and then run the [database/prepare_openings.py](database/prepare_openings.py) script inside `.../chess-openings/dist`.
Ultimately, place the resulting `openings.sql` file within [database](database) folder.

You also must have Stockfish downloaded for the games with bots to work.
Download [the Stockfish executable](https://stockfishchess.org/download/) for your platform
and [nn-1c0000000000.nnue](https://tests.stockfishchess.org/api/nn/nn-1c0000000000.nnue).
You can put them in the same directory as the config.
Then, point the executable field in `config.yml` to your executable,
and the `EvalFile` field to the nnue.

# Building
To build the app itself, run the Gradle task `composeApp:run`.
This task uses jOOQ to generate sources for the database schema,
so you must have the database running and up to date when building. To quickly set up the schema, run `database/create.sh`.

# Testing
If you want to run all the automated tests, run `gradle allTests`.

Some automated tests in `shared/src/jvmTest/` use large external PGN files. 
These tests are not run by default, as they are very time-consuming. 

If you want to run the full test suite, add
```
runExternalTests=true
```
to the `local.properties` file in `local/` and create a `pgn_database.pgn`
file in `local/` containing example pgn data. These files in are accessed through the `shared/local.properties` and `shared/src/jvmTest/pgn_database.pgn` symlinks, and symlinks uploaded to Git do not always work on platforms other than Linux, so you might need to change them to platform-specific links if you're having trouble with making the tests work.

An example pgn database to use is approximately the first 100k lines of [this file](https://lichess.org/api/games/user/german11).

# Debugging
To change the log level, add:
```
log.level=<LEVEL>
```
to the `local.properties` file in `local/`. Like before, this file is accessed through symlinks in `composeApp/`, `shared/` and `server/`, so they might need to be modified on platforms other than Linux. 

The available levels are `ERROR`, `WARN`, `INFO` (default), `DEBUG` and `TRACE`.

# Project structure
The [OVERVIEW.md](OVERVIEW.md) file details the general project structure and how the different modules are linked together.

# Licences
- **Fresca** chess piece set from [Lichess.org GitHub](https://github.com/lichess-org/lila/blob/master/COPYING.md):
  [CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/)
