# Overview
The app is written in Kotlin and uses its features extensively.
It consists of three modules:
- `shared`
- `server`
- `composeApp`

# Client-server architecture
While currently the server is bundled with the Compose application code,
the project structure allows a networked architecture to be implemented very easily.

All the communication with the server is done by the `ClientApi` interface defined in
the `shared` module.
It is implemented by the `Server` class in the `server` module.
The repo also includes a demo network layer implementation, which demonstrates how easy it would
be to implement without any changes to the server implementation.

# Accounts
The app does not currently have support for multiple users. The database is, however, implemented
with full account support. To make it work on the PO side, the app is always "logged in"
as a specific, special user account, defined in the configuration file.

# Compose app
The desktop app is made using [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/).
This framework is not yet mature, but as a declarative framework it is a very elegant (in terms of code structure)
way of creating user interfaces. 

The app code is split into two parts:
- `viewmodel`
- `view`

`view` stores `@Composable` components, while `viewmodel` contains classes responsible for 
 data holding and business logic.

To improve code structure and avoid duplication, there are generic data-managing classes:

## `DataStateViewModel`
An instance of this class is created by passing a single function for loading data for that model.
It provides a simple way to reload data, show a progress indicator and handle and display errors.

To use it in the UI, the `DataStateAdapter` can be used. It displays the loading and errors
states automatically and still gives the user enough options to create custom content interfaces. 

## `Paging`
A more interesting sibling of the `DataStateViewModel` class is the `Sibling` interface and implementation.
`Paging` manages loading consecutive pages of an infinite list.
A `Paging` instance handles errors by pausing the fetching of extra pages until the error is dismissed.

Together with the `PagingAdapter` it provides infinite vertical scrolling support.

## `DismissibleErrorsState`
This class is used for simpler views that can error. It comes together with a `DismissibleErrorsAdapter`.

## Model

All six classis chess pieces were implemented as an extension of the `Piece` class. The key methods of these pieces are `getMoveVision` and `getCaptureVision` - in a given state of the board the pieces have the capacity to output the squares they would be able to move to, if given a move.\
The key feature of the model is the `BoardState` class, which wraps a `Board` with methods that allow for extracting complex information and applying a move.
We decided to make the `BoardState` immutable - the application of a move results in another `BoardState` describing a state of the board after the move has been applied being created.
This class also includes complete move legality validation, game over detection and conversion to and from several data types describing chess games or moves.

The moves or board states in chess can be represented in many ways. The model implements conversion to and from among all from the list below:

- Long Algebraic Notation - the simplest representation of a chess move. Describes the source square, destination square and optionally the piece to promote to. This is the format used by bots within our project.
- Short Algebraic Notation - a human-readable format of the long algebraic notation. When not necessary, it omits the source square, instead including the moving piece, information of captures, checks, checkmate or castling.
- Forsyth-Edwards Notation - this notation describes the entirety of a state of the board, and thus, it is directly serializable to and from `BoardState`. It holds information like the contents of the board, player to move, players' castling rights, en passant possibility and move counters.
- Portable Game Notation - this format is most often used to describe entire games, by a series of moves accompanied by a set of metadata describing a game. The model has been extensively tested for any edge cases with thousands of chess games and supports methods that later allow for importing or exporting the PGN format of a game.

## Util

# Server

## Bot

This package consists mainly of one class - `Bot`. It implements a connection with an external bot executable over the [UCI Protocol](https://backscattering.de/chess/uci/), allowing for easy use of practically any modern chess bot.

The bot works very similarly to a real player, taking `GameObserver` and `GameInput` objects when it's starting playing a game, meaning that `LiveGame` can work exactly the same way with bots as it would with real players.

## Config

## External

## Server

## Tournament

The final planned component of the project. Unfortunately, the complete implementation of this feature did not fit within the project's deadline.

Despite that, however, the functionality has thought out and the database was prepared for linking with the project.

In the planned implementation, a tournament in the Swiss system would be represented by a `Tournament` class, with its factory class `TournamentFactory` to manage correct communication with the database.
Swiss-system tournaments are played across a set number of rounds, within each of which all current participants are grouped into pairs.
The implementation would have allowed for the current standings of the tournament to be shown at any point in time.

The class `TournamentMatchingUnit` was implemented as groundwork for this feature and tested on a small scale.
The pairing system relies on a deterministic, greedy variation of the [Monrad system](https://en.wikipedia.org/wiki/Swiss-system_tournament#Monrad_system).
Duplicate games are not allowed, and the pairing system is built in a way to minimalize the point difference between players, while minimalizing the number of unpaired players.
The chosen algorithm likely would not provide a perfect (or near-perfect) matching in some cases, especially in later rounds. With forced matching, however, this algorithm would be a sufficient fit for the small-scale tournaments, likely played between bots.
