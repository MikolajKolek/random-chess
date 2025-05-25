# random-chess
A chess app with functionality for saving, analyzing and playing games.

# Building
Before building the app, you must have postgresql installed and running on your machine with the appropriate schema.
To quickly set up the schema, run `database/create.sh`.

To build the app itself, run the Gradle task `composeApp:run`.

You also must have Stockfish downloaded, so the bot games can work.
Download [the Stockfish executable](https://stockfishchess.org/download/) for your platform
and [nn-1c0000000000.nnue](https://tests.stockfishchess.org/api/nn/nn-1c0000000000.nnue).
Then, point the executable field in [composeApp/config.yml](composeApp/config.yml) to your executable,
and the `EvalFile` field to the nnue. 

# Debugging
To change the log level, create a local.properties file in `composeApp/` and add:
```
log.level=<LEVEL>
```
The available levels are `ERROR`, `WARN`, `INFO` (default), `DEBUG` and `TRACE`.