# random-chess
A chess app with functionality for saving, analyzing and playing games.

# Getting started
Copy the config file from [local/config.example.yml](local/config.example.yml) to:
- on Linux: `~/.local/share/rchess/config.yml`
- on Windows: `%APPDATA%/rchess/config.yml`
- on macOS: `~/Library/Application Support/rchess/config.yml`

You also must have Stockfish downloaded, so the bot games can work.
Download [the Stockfish executable](https://stockfishchess.org/download/) for your platform
and [nn-1c0000000000.nnue](https://tests.stockfishchess.org/api/nn/nn-1c0000000000.nnue).
You can put them in the same directory as the config.
Then, point the executable field in `config.yml` to your executable,
and the `EvalFile` field to the nnue.

Before building the app, you must have postgresql installed and running on your machine with the appropriate schema.
To quickly set up the schema, run `database/create.sh`.

# Building
To build the app itself, run the Gradle task `composeApp:run`.
This task uses jooq to generate sources for the database schema,
so you must have the database running and up to date when building.

# Testing
If you want to run all the automated tests, run `gradle allTests`.

Some automated tests in `shared/src/jvmTest/` use large external PGN files. 
These tests are not run by default, as they are very time-consuming. 

If you want to run the full test suite, add
```
runExternalTests=true
```
to the `local.properties` file in `local/` and create a `pgn_database.pgn`
file in `local/` containing example pgn data.

An example pgn database to use is approximately the first 100k lines of [this file](https://lichess.org/api/games/user/german11).

# Debugging
To change the log level, add:
```
log.level=<LEVEL>
```
to the `local.properties` file in `local/`.

The available levels are `ERROR`, `WARN`, `INFO` (default), `DEBUG` and `TRACE`.

# Licences
- **Fresca** chess piece set from [Lichess.org GitHub](https://github.com/lichess-org/lila/blob/master/COPYING.md):
  [CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/)
