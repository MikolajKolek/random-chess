# Overview
The app is written in Kotlin and uses its features extensively.
It consists of three modules:
- `shared`
- `server`
- `composeApp`

# Client-server architecture
While currently the server is bundled with the Compose application code,
the project structure allows a networked architecture to be implemented very easily.

All the communication with the server is done using the `ClientApi` interface defined in
the `shared` module.
It is implemented by the `Server` class in the `server` module.
The repo also includes a demo network layer implementation, which demonstrates how easy it would
be to implement without any changes to the `Server` implementation.

# Accounts
The app does not currently have support for multiple users. The database is, however, implemented
with full account support. To make it work on the application side, the app is always "logged in"
as a specific, special user account, defined in the configuration file.

# Compose app
The desktop app is made using [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/).
The framework is not yet mature, but as a declarative framework it is a very elegant (in terms of code structure)
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
A more interesting sibling of the `DataStateViewModel` class is the `Paging` interface and implementation.
`Paging` manages loading consecutive pages of an infinite list.
A `Paging` instance handles errors by pausing the fetching of extra pages until the error is dismissed.

Together with the `PagingAdapter` it provides infinite vertical scrolling support.

## `DismissibleErrorsState`
This class is used for simpler views that can error. It comes together with a `DismissibleErrorsAdapter`.

## Colors
Colors used in the project are defined in the `RandomChessTheme.kt` file:
- `primary`, `primaryContainer` are used for the most prominent elements on screen,
  used especially for constructive actions (starting a game, importing games)
- `secondary`, `secondaryContainer` are used for selection indication and actionable elements
- `tertiary`, `tertiaryContainer` are used to focus user attention on otherwise less prominent elements. 
 In a game they are used for indicating the current turn - clock highlights, game progress, possible piece moves
- `surface`, `background` - used for the lowest layer background
- `surfaceContainer` and variants - used for the backgrounds of cards and other containers

# Shared
This module contains logic shared between the client and the server.

## Api

The `api` package contains objects used for communication between the `client` and `server` modules. An important interface is `ClientApi`, used for all direct communication between the client and the server. 

The `entity` subpackage contains objects returned from the server to the client on api requests. `args` on the other hand contains objects used as arguments in more complicated `ClientApi` methods.

## Model

All six classic chess pieces were implemented as an extension of the `Piece` class. The key methods of these pieces are `getMoveVision` and `getCaptureVision` - in a given state of the board, the pieces have the capacity to output the squares they are able to move to.\
The key feature of the model is the `BoardState` class, which wraps a `Board` with methods that allow for extracting complex information and applying a move.
We decided to make the `BoardState` immutable - the application of a move results in another `BoardState` describing a state of the board after the move has been applied being created.
This class also includes complete move legality validation, game over detection, and conversion to and from several data types describing chess games or moves.

The moves or board states in chess can be represented in many ways. The model implements conversion to and from all the formats listed below:

- Long Algebraic Notation - the simplest representation of a chess move. Describes the source square, destination square, and optionally the piece to promote to. This is the format used by bots implementing the UCI protocol.
- Short Algebraic Notation - a human-readable format of the long algebraic notation. When not necessary, it omits the source square, instead including the moving piece, information of captures, checks, checkmate, or castling.
- Forsyth-Edwards Notation - this notation describes the entire state of the board, and thus, it is directly serializable to and from `BoardState`. It holds information like the contents of the board, the player to move, players' castling rights, en passant possibility, and move counters.
- Portable Game Notation - this format is most often used to describe entire games, by a series of moves accompanied by a set of metadata values. The model has been extensively tested for any edge cases with thousands of chess games in the PGN format.

## Util

The `util` module contains various utility classes and functions. This includes, for example, the `SingleTaskTimer` used by the `LiveGameController` to implement timeouts, the logger used in various parts of the project, and different extension functions used to add functionality to preexisting Kotlin objects.

# Server

The server module is responsible for all the functionality that would end up on the server in a client-server scenario.

To make sure that the client does not use the server in any way other than using a `Server` instance as a `ClientApi`, everything in this module besides the `ApiProvider` is marked as `internal`, meaning that access is only allowed from the same module.

## Bot

This package consists mainly of one class - `Bot`. It implements a connection with an external bot executable over the [UCI Protocol](https://backscattering.de/chess/uci/), allowing for easy use of practically any modern chess bot.

The bot works very similarly to a real player, taking `GameObserver` and `GameInput` objects when it's starting playing a game, meaning that the `LiveGameController` can work exactly the same way with bots as it would with real players.

## Config

This package holds the different data classes used for storing config data. Additionally, it contains `ConfigLoader`, which manages loading the config and makes sure the correct path is used on all platforms.

## External

## Server

This package contains the `Server` class, which is responsible for all the communication with the database and responding to client requests. `Server` implements `ClientApi` for communication with the client, but also `Database`, which is used internally in the server for database access. This allows for simple testing of server components, as mock Database instances can easily be created.

Serialization and deserialization from records created by jOOQ is handled by the `Serialization` object. It makes use of one of Kotlin's unique features - extension functions - to elegantly add functionality to existing classes from the `api` package.

New games are launched by the `Server` using a `GameWithBotFactory`, which is used to spawn new games with bots. In the background, it creates a `LiveGameController` which is a class used for managing a live game. It's responsible for keeping track of timers, checking game end conditions, making moves on the internal state machine, and much more. It's important to note that its structure allows it to be used not only for games with bots, but also other kinds of games, with no modification required. This means that it could be reused for live games between two players if a client-server architecture was being implemented.

## Tournament

The final planned component of the project. Unfortunately, the complete implementation of this feature did not fit within the project's deadline.

Despite that, however, the functionality has been thought out and the database was prepared for linking with the application.

In the planned implementation, a tournament in the Swiss system would be represented by a `Tournament` class, with its factory class `TournamentFactory` to manage correct communication with the database.
Swiss-system tournaments are played across a set number of rounds, within each of which all current participants are grouped into pairs.
The implementation would have allowed for the current standings of the tournament to be shown at any point in time.

The class `TournamentMatchingUnit` was implemented as groundwork for this feature and tested on a small scale.
The pairing system relies on a deterministic, greedy variation of the [Monrad system](https://en.wikipedia.org/wiki/Swiss-system_tournament#Monrad_system).
Duplicate games are not allowed, and the pairing system is built in a way to minimalize the point difference between players, while minimalizing the number of unpaired players.
The chosen algorithm likely would not provide a perfect (or near-perfect) matching in some cases, especially in later rounds. With forced matching, however, this algorithm would be a sufficient fit for the small-scale tournaments, likely played between bots.
