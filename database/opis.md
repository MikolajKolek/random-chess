# Tematyka i cele projektu

Projekt ***Random Chess*** został stworzony w celu ogranizacji treningu szachowego poprzez agregowanie partii z różnych źródeł oraz rozgrywania lokalnych partii z botami offline. Zaimpelementowane funkcjonalności obejmują:

- Rozgrywanie partii w aplikacji z botem
    - Weryfikacja legalności ruchów graczy
    - Integracja z istniejącymi silnikami szachowymi - obecnie [Stockfish](https://stockfishchess.org/) na 4 różnych poziomach trudności
- Historia rozegranych partii, włączając:
    - Partie zaimportowane w formacie [PGN – Portable Game Notation](https://pl.wikipedia.org/wiki/Portable_Game_Notation)
    - Partie automatycznie pobierane z połączonych kont na portalu [lichess.org](https://lichess.org/api)
    - Partie rozegrane w naszym serwisie
- Możliwość eksportowania partii z historii w formacie PGN
- Analiza partii
    - Rozpoznawanie debiutów
    - Wyświetlanie ruchów w [notacji algebraicznej](https://pl.wikipedia.org/wiki/Szachowa_notacja_algebraiczna)
- Utrzymanie struktury klient-serwer w celu potencjalnego rozszerzenia projektu
- Szacowanie rankingu ELO wśród partii rozegranych lokalnie - osobno dla różnych rodzajów temp rozgrywki
- Przeprowadzanie turniejów w systemie szwajcarskim (tylko w bazie, w aplikacji moduł do parowania graczy)

# Schemat bazy

## Typy i domeny

### game_result

Domena `game_result` na typie `game_result_type` przechowuje informacje o rozstrzygnięciu partii oraz o powodzie zakończenia gry, np. `(1-0,TIMEOUT)` lub '(1/2-1/2,FIFTY_MOVE_RULE)'

### clock_settings

Domena `clock_settings` na typie `clock_settings_type` przetrzymuje informacje o tempie partii - czasie przydzielanym początkowo oraz czasie przyznawanym po każdym ruchu.

## Tabele

### openings


| Pole          | Typ          | Dodatkowe informacje |
| ------------- | ------------ | -------------------- |
| **`id`**      | SERIAL       | **PRIMARY KEY**      |
| `eco`         | CHAR(3)      | NOT NULL             |
| `name`        | VARCHAR(256) | NOT NULL             |
| `partial_fen` | VARCHAR      | UNIQUE NOT NULL      |

Tabela ta przechowuje dane o wszystkich debiutach szachowych poprzez ich nazwę, kod z [Encyclopedia of Chess Opening](https://en.wikipedia.org/wiki/Encyclopaedia_of_Chess_Openings), nazwę i pozycję reprezentującą ten debiut.
Debiut charakteryzowany jest przez układ szachownicy, gracza przy ruchu oraz możliwość wykonania roszady/en passant przez obu graczy.
Dane do tej tabeli importujemy z [bazy danych lichess.org](https://github.com/lichess-org/chess-openings) - w tym celu przygotowaliśmy instrukcję oraz skrypt.

### users


| Pole            | Typ     | Dodatkowe informacje |
|-----------------|---------|----------------------|
| **`id`**        | SERIAL  | **PRIMARY KEY**      |
| `email`         | VARCHAR | UNIQUE NOT NULL      |
| `password_hash` | VARCHAR | NOT NULL             |

W tej tabeli przechowywane są dane o lokalnych użytkownikach. Tabela ta jest stosunkowo niewielka ze względu na to, że większość danych przechowywana jest w bardziej uniwersalnej `service_accounts`.
Kolumna `elo`, która znajdowała się tutaj poprzednio została zastąpiona kompletnym i funkcjonalnym systemem rankingowym [ELO](https://en.wikipedia.org/wiki/Elo_rating_system) na podstawie lokalnie rozgrywanych partii.
Poprawność adresu mailowego jest obłożona checkiem na podstawie wyrażenia regularnego.

### game\_services


| Pole     | Typ          | Dodatkowe informacje |
| -------- | ------------ | -------------------- |
| **`id`** | SERIAL       | **PRIMARY KEY**      |
| `name`   | VARCHAR(256) | UNIQUE NOT NULL      |

Jest to dość niewielka tabela przechowująca różne źródła gier niezaimportowanych przez PGN.
Przechowujemy tutaj informację o serwisach, z którymi aplikacja wspiera integrację.
W szczególności, pod indeksem `1` znajduje się wpis `Random Chess`, opisujący lokalne partie.
To oznacza, że lokalne partie traktujemy na podstawowym poziomie tak samo, jak te zaimportowane z innych serwisów, co pozwala nam przechowywać je w jednej tabeli `service_games`.

### service\_accounts

| Pole                     | Typ           | Dodatkowe informacje                    |
|--------------------------|---------------|-----------------------------------------|
| `user_id`                | INT           | REFERENCES users(id) ON DELETE SET NULL |
| **`service_id`**         | INT           | NOT NULL REFERENCES game\_services(id)  |
| **`user_id_in_service`** | VARCHAR       | NOT NULL                                |
| `token`                  | VARCHAR       | NULL                                    |
| `display_name`           | VARCHAR(256)  | NOT NULL                                |
| `is_bot`                 | BOOL          | NOT NULL                                |

`token` to wartość trzymana dla połączonych kont z zewnętrznych aplikacji, służąca do synchronizacji partii z nimi. Jest ona niepusta dla zewnętrznych kont nieusuniętych użytkowników.

Para **`(service_id, user_id_in_service)`** tworzy klucz podstawowy. Każdemu użytkownikowi serwisu szachowego może odpowiadać co najwyżej jeden użytkownik w naszym systemie kont.

Boty, zarówno w naszym serwisie, jak i serwisach zewnętrznych, posiadają `service_account`, ale z `user_id IS NULL` i `is_bot = TRUE`.

`service_account` istnieje dla każdego użytkownika, który połączył swoje konto w naszym serwisie z dowolnym serwisem szachowym, ale też dla użytkowników, którzy nie mają kont w naszym serwisie, a są dowolną ze stron partii przechowywanej w `service_games`. Podjęliśmy tę decyzję, ponieważ jeśli użytkownik taki później utworzy konto w naszym serwisie, to nie chcemy musieć dodawać tej samej partii drugi raz do `service_games` ani modyfikować partii w `service_games`. Zamiast tego, po podłączeniu konta jego `user_id` zostanie po prostu podłączone do już istniejącego `service_account` i będziemy musieli wyłącznie pobrać brakujące partie z odpowiedniego API do `service_games`.

Konsekwencją tej decyzji jest to, że nie chcemy nigdy usuwać `service_account` i zamiast tego po usunięciu użytkownika odłączamy od niego wszystkie jego `service_accounts`.
Jest to realizowane poprzez ustawienie `ON DELETE SET NULL` w polu `user_id`.

Dodatkowo każdy użytkownik naszego serwisu posiada dokładnie jedno odpowiadające konto w tabeli `service_accounts` o `service_id` = `1` (id naszego serwisu szachowego). Konto to przechowuje jego nazwę użytkownika w naszym serwisie, a jego `user_id_in_service` = `user_id`.
Jest to redundancja, ale:
1. ponieważ zdecydowaliśmy się na klucz podstawowy `(service_id, user_id_in_service)`, `user_id_in_service` musi być różne dla każdego użytkownika,
2. po usunięciu konta użytkownika jego odpowiadający `service_account` musi dalej istnieć (aby partie rozegrane z nim nie zniknęły). Te różne `service_accounts` w naszym serwisie pozostałe po usuniętych użytkownikach muszą być w jakiś sposób rozróżnialne i tym sposobem jest właśnie `user_id_in_service`.

Wyzwalacze `add_default_service_to_user`, `prevent_default_service_modification` oraz `prevent_default_service_deletion` służą upewnieniu się, że od utworzenia użytkownika po jego usunięcie jego odpowiadające konto o `sevice_id = 1` w `service_accounts` będzie zawsze istnieć.

Dodatkowo, ograniczenie `valid_system_account` w `service_accounts` upewnia się, że dla kont tych spełnione są powyższe założenia:
- dla użytkowników, póki ich konto istnieje, to `user_id_in_service = user_id`,
- dla botów `user_id IS NULL`.

### Tabele service\_games i pgn\_games

Te tabele przechowują partie szachowe, które będą analizowane w naszej aplikacji. Tabela `service_games` przechowuje zsynchronizowane partie z zewnętrznych serwisów oraz partie rozegrane w naszym serwisie. Tabela `pgn_games` przechowuje partie, które zostały zaimportowane ręcznie przez użytkownika.

[W późniejszej sekcji](#modelowanie-partii-szachowych) opisaliśmy, dlaczego zdecydowaliśmy się zamodelować partie szachowe właśnie w ten sposób.

Klucze podstawowe **`id`** w `service_games` i `pgn_games` mogą się powtarzać.


#### Wspólne pola w tabelach service\_games i pgn\_games

| Pole                | Typ            | Dodatkowe informacje                                                      |
|---------------------|----------------|---------------------------------------------------------------------------|
| `moves`             | VARCHAR(5)[]   | NOT NULL                                                                  |
| `starting_position` | VARCHAR(100)   | NOT NULL                                                                  |
| `partial_fens`      | VARCHAR[]      | GENERATED ALWAYS AS (generate_fen_array(starting_position, moves)) STORED |
| `creation_date`     | TIMESTAMPTZ    | NOT NULL                                                                  |
| `result`            | GAME_RESULT    | NOT NULL                                                                  |
| `metadata`          | JSOB           | NULL                                                                      |
| `clock`             | CLOCK_SETTINGS | NULL                                                                      |

Jako że wszystkie partie posiadają duże przecięcie, niezależnie od źródła, wiele pól występuje zarówno w tabeli `service_games` jak i w `pgn_games`.

Kolumna `moves` przechowuje ruchy graczy w partii w postaci [PGN](https://pl.wikipedia.org/wiki/Portable_Game_Notation), bez metadanych.

Kolumna `starting_position` przetrzymuje pierwsze 4 pola formatu [FEN](https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation).

Kolumna `partial_fens` przechowuje kolejne stany w postaci jak powyżej wszystkich ruchów od początku do końca partii.
Jest to kolumna wygenerowana funkcją na podstawie listy ruchów i startowej pozycji.
Zdecydowaliśmy się przetrzymywać to pole na stałe, gdyż zapytania do tej tabeli są bardzo częste, a obliczenie tego pola relatywnie czasochłonne.

Kolumna `creation_date` opisuje datę rozegrania albo importu, w zależności od rodzaju partii.

Kolumna `result` typu `game_result` przetrzymuje sposób zakończenia rozgrywki, jak opisano w sekcji `Typy i domeny`.

Kolumna `metadata` zawiera wszystkie niestandardowe pola metadanych pochodzących z opisu partii w postaci PGN. Dane przechowujemy w formacie JSON, choć nie spełnia to reguły atomowości, bo dokładny ich format może się różnić w zależności od serwisu, a dane te służą jedynie do wyświetlenia użytkownikowi i ponownego eksportu rozgrywki do formatu PGN, nigdy nie będziemy wykonywać zapytań dotyczących metadanych w tym polu.

Kolumna `clock` w typie `clock_settings` przetrzymuje dane o tempie, w którym partia została rozegrana. Wartość ta poza ponownym eksportem do formatu PGN służy także przy obliczaniu rankingów.

#### Pola występujące tylko w service\_games

| Pole                 | Typ     | Dodatkowe informacje                   |
|----------------------| ------- |----------------------------------------|
| **`id`**             | SERIAL  | **PRIMARY KEY**                        |
| `game_id_in_service` | VARCHAR | NULL                                   |
| `service_id`         | INT     | NOT NULL REFERENCES game\_services(id) |
| `white_player`       | VARCHAR | NOT NULL                               |
| `black_player`       | VARCHAR | NOT NULL                               |
| `is_ranked`          | VARCHAR | NOT NULL                               |

`game_id_in_service` to ID pochodzące z zewnętrznego API. Na pary `(game_id_in_service, service_id)` jest założone ograniczenie UNIQUE.

Dla partii pochodzących z naszego serwisu, `game_id_in_service` jest NULL, ponieważ `id` identyfikuje je jednoznacznie. Tabela ma ograniczenie upewniające się, że dla każdej takiej partii `game_id_in_service IS NULL`, a dla wszystkich partii pochodzących z zewnętrznych serwisów `game_id_in_service IS NOT NULL`.

Dla zewnętrznych serwisów `white_player` i `black_player` oznaczają id użytkownika w API tego serwisu. Pary `(white_player, service_id)` i `(black_player, service_id)` są kluczami obcymi wskazującymi na pary `(service_id, user_id_in_service)`, czyli klucz podstawowy, w tabeli `service_accounts`.

Pole `is_ranked` (fałszywe dla gier spoza naszego serwisu) opisuje, czy dana rozgrywka powinna być liczona do rankingów.

#### Pola występujące tylko w pgn\_games

| Pole                | Typ     | Dodatkowe informacje                            |
| ------------------- | ------- |-------------------------------------------------|
| **`id`**            | SERIAL  | **PRIMARY KEY**                                 |
| `owner_id`          | INT     | NOT NULL REFERENCES users(id) ON DELETE CASCADE |
| `white_player_name` | VARCHAR | NOT NULL                                        |
| `black_player_name` | VARCHAR | NOT NULL                                        |

`owner_id` to ID użytkownika, który zaimportował daną partię. Ponieważ w przypadku `pgn_games` partie widzi tylko właściciel, pole to ma ustawione `ON DELETE CASCADE`, aby po jego usunięciu partia także została usunięta.

### rankings

CREATE TABLE rankings(
"id"                    SERIAL      PRIMARY KEY,
"name"                  VARCHAR     NOT NULL,
-- Inclusive:
"playtime_min"          INTERVAL    NOT NULL    CHECK ("playtime_min" >= '0 seconds'::INTERVAL),
-- Exclusive:
"playtime_max"          INTERVAL    NULL        CHECK ("playtime_max" > '0 seconds'::INTERVAL),
"extra_move_multiplier" INT         NOT NULL    CHECK ("extra_move_multiplier" >= 0),
"starting_elo"          NUMERIC     NOT NULL    CHECK ("starting_elo" > 0),
"include_bots"          BOOLEAN     NOT NULL,
"k_factor"              NUMERIC     NOT NULL,

    CONSTRAINT "playtime_valid" CHECK ("playtime_max" IS NULL OR "playtime_min" <= "playtime_max")
);

### elo_history

CREATE TABLE elo_history(
"id"                    SERIAL      PRIMARY KEY,
"service_id"            INT         NOT NULL    REFERENCES "game_services" ("id")
CHECK ("service_id" = 1),
"user_id_in_service"    VARCHAR     NOT NULL,
"ranking_id"            INT         NOT NULL    REFERENCES "rankings" ("id"),
"game_id"               INT         NOT NULL    REFERENCES "service_games" ("id"),
"elo"                   NUMERIC     NOT NULL,
"previous_entry"        INT         NULL,

    -- Make sure that a single elo history entry is not the previous entry more than once
    UNIQUE NULLS NOT DISTINCT ("service_id", "user_id_in_service", "ranking_id", "previous_entry"),

    -- A single game can only influence a player's elo once
    UNIQUE("service_id", "user_id_in_service", "ranking_id", "game_id"),

    UNIQUE("service_id", "user_id_in_service", "ranking_id", "id"),
    FOREIGN KEY("service_id", "user_id_in_service", "ranking_id", "previous_entry")
        REFERENCES "elo_history"("service_id", "user_id_in_service", "ranking_id", "id"),

    FOREIGN KEY ("service_id", "user_id_in_service")
        REFERENCES "service_accounts" ("service_id", "user_id_in_service")
);

### swiss\_tournaments

| Pole                | Typ             | Dodatkowe informacje         |
|---------------------|-----------------|------------------------------|
| **`tournament_id`** | SERIAL          | **PRIMARY KEY**              |
| `round_count`       | INT             | NOT NULL                     |
| `starting_position` | VARCHAR         | NOT NULL                     |
| `is_ranked`         | BOOLEAN         | NOT NULL                     |
| `ranking_id`        | INTEGER         | NULL REFERENCES rankings(id) |
| `time_control`      | CLOCK_SETTINGS  | NOT NULL                     |

`round_count` to informacja o maksymalnej liczbie rund, w jakiej rozgrywany jest dany turniej-wartość charakterystyczna dla turniejów w systemie szwajcarskim.

`starting_position` to wartość startowej pozycji obowiązująca wszystkie rozgrywki zaliczane do danego turnieju, podobnie jak `time_control` zadaje wymaganie o konkretnym tempie rozgrywki.

`ranking_id` wskazuje na jeden z typów rankingu, który będzie używany do obliczania performance rating dla tego turnieju (o tym więcej w widoku `swiss_tournaments_players_points`). Ta wartość musi być niepusta nawet dla turniejów nierankingowych. Wymagamy także, aby `time_control` było zgodne z podanym typem rankingu.

`is_ranked` opisuje, czy dany turniej jest rankingowy. Rankingowe turnieje wymagają, by wszystkie ich gry były rankingowe, i odwrotnie.

### tournaments\_games

| Pole            | Typ | Dodatkowe informacje                                 |
|-----------------|-----|------------------------------------------------------|
| `tournament_id` | INT | NOT NULL REFERENCES swiss_tournaments(tournament_id) |
| `game_id`       | INT | NOT NULL RERERENCES service_games(id)                |
| `round`         | INT | NOT NULL                                             |

Tabela ta łączy turnieje z zarejestrowanymi dla niego partiami. Zaimpelementowane triggery i checki zapewniają, że rozgrywka zgodna jest z wymaganiami zapewnionymi przez turnieju oraz że gracze są w turnieju zarejestrowani.

Nałożona na kolumnę `tournament_id` klauzula `ON DELETE CASCADE` zapewnia, że gra zostanie usunięta z tego rejestru, gdy zostanie usunięty jej turniej.

### tournaments\_players


| Pole                 | Typ      | Dodatkowe informacje                                 |
|----------------------|----------|------------------------------------------------------|
| `service_id`         | INT      | NOT NULL DEFAULT 1 REFERENCES game_services(id)      |
| `tournament_id`      | INT      | NOT NULL RERERENCES swiss_tournaments(tournament_id) |
| `user_id_in_service` | VARCHAR  | NOT NULL                                             |

Tabela ta łączy turnieje z zarejestrowanymi graczami. Przy dołączeniu sprawdzana jest zgodność z wymaganiami nałożonymi na dany turniej.

Pole `service_id` stale ustawione na `1` jest w celu ustawienia klucza obcego złożonego z pól `service_id, user_id_in_service` na tabelę `service_accounts`.
To dość hakerskie rozwiązanie i ta wartość jest właściwie zbędna (stosujemy je też w innym miejscu), ale język nie pozwala nam wstawić stałej wartości do klucza.

Dodatkowy trigger zapobiega usuwaniu z tej tabeli zawodników tak długo, jak ich partie są zarejestrowane do danego turnieju.

### byes

| Pole                 | Typ      | Dodatkowe informacje                                 |
|----------------------|----------|------------------------------------------------------|
| `tournament_id`      | INT      | NOT NULL RERERENCES swiss_tournaments(tournament_id) |
| `round`              | INT      | NOT NULL DEFAULT 1 REFERENCES game_services(id)      |
| `user_id_in_service` | VARCHAR  | NOT NULL                                             |

Istnienie tej tabeli jest koniecznością ze względu na sposób działania parowania systemu szwajcarskiego. Wymagania nałożone na ten system oraz choćby nawet nieparzysta liczba graczy sprawia, że może nie udać się sparować wszystkich graczy w danej rundzie.
Gracz niesparowany otrzymuje w ten sposób darmowy punkt.

### tournaments_ranking_reqs

| Pole             | Typ     | Dodatkowe informacje                                 |
|------------------|---------|------------------------------------------------------|
| `tournament_id`  | INT     | NOT NULL REFERENCES swiss_tournaments(tournament_id) |
| `ranking_type`   | INT     | NOT NULL REFERENCES rankings(id) DEFAULT 1           |
| `required_value` | NUMERIC | NOT NULL                                             |

Tabela ta opisuje nałożone na dany turniej wymaganie minimum punktów ELO osiągniętych w danym rankingu.

Wpisy z tej tabeli są usuwane po usunięciu odpowiadającego im turnieju.

### tournaments_ranked_games_reqs

| Pole             | Typ | Dodatkowe informacje                                 |
|------------------|-----|------------------------------------------------------|
| `tournament_id`  | INT | NOT NULL REFERENCES swiss_tournaments(tournament_id) |
| `ranking_type`   | INT | NOT NULL REFERENCES rankings(id) DEFAULT 1           |
| `game_count`     | INT | NOT NULL                                             |

Tabela ta opisuje nałożone na dany turniej wymaganie rozegrania pewnej liczby rankingowych gier zaliczonych do danego typu rankingu.

Podobnie jak inne wpisy łączące się z danym turniejem, wpisy z tej tabeli także są usuwane po usunięciu turnieju.

## Widoki

### games

| Pole                    | Typ            | Dodatkowe informacje                                                      |
|-------------------------|----------------|---------------------------------------------------------------------------|
| `id`                    | INT            | NOT NULL                                                                  |
| `kind`                  | VARCHAR        | Jeden z (`'service'`, `'pgn'`)                                            |
| `starting_position`     | VARCHAR(100)   | NOT NULL                                                                  |
| `moves`                 | VARCHAR(5)[]   | NOT NULL                                                                  |
| `partial_fens`          | VARCHAR[]      | GENERATED ALWAYS AS (generate_fen_array(starting_position, moves)) STORED |
| `creation_date`         | TIMESTAMPTZ    | NOT NULL                                                                  |
| `result`                | GAME_RESULT    | NOT NULL                                                                  |
| `metadata`              | JSOB           | NULL                                                                      |
| `clock`                 | CLOCK_SETTINGS | NULL                                                                      |
| `result`                | GAME_RESULT    | NOT NULL                                                                  |
| `service_id`            | INT            | NULL (puste dla kind `'pgn'`)                                             |
| `white_service_account` | VARCHAR        | NULL (puste dla king `'pgn'`)                                             |
| `black_service_account` | VARCHAR        | NULL (puste dla kind `'pgn'`)                                             |
| `is_ranked`             | BOOLEAN        | NULL (puste dla kind `'pgn'`)                                             |
| `pgn_owner_id`          | INT            | NULL (puste dla kind `'service'`)                                         |
| `pgn_white_player_name` | VARCHAR        | NULL (puste dla kind `'service'`)                                         |
| `pgn_black_player_name` | VARCHAR        | NULL (puste dla kind `'service'`)                                         |

Widok `games` jest UNION `service_games` i `pgn_games`. `kind` jest równy `'service'` dla partii pochodzących z `service_games` i `'pgn'` dla partii pochodzących z `'pgn_games'`. `id` nie jest unikatowe dla wszystkich jego elementów, ale para `(id, kind)` już jest.

W szczególności, wartości unikalne dla jednego z tych rodzajów są puste dla wartości pochodzących z drugiego.

### users\_games

| Pole                   | Typ          | Dodatkowe informacje                               |
|------------------------|--------------|----------------------------------------------------|
| `user_id`              | INT          | NOT NULL, dotyczy `service_accounts`               |
| `game_id`              | INT          | NOT NULL, dotyczy `service_games` albo `pgn_games` |
| `kind`                 | INT          | Jeden z (`'service'`, `'pgn'`)                     |
| `moves`                | VARCHAR(5)[] | NOT NULL                                           |
| `creation_date`        | TIMESTAMPTZ  | NOT NULL                                           |
| `result`               | GAME_RESULT  | NOT NULL                                           |
| `metadata`             | JSOB         | NULL                                               |

Widok ten łączy użytkowników z rozegranymi przez nich grami-tutaj także znajduje się pole `kind` oznaczające źródło pochodzenia partii.
Podobnie jak wcześniej, para (`game_id`,`kind`) jest unikalna, mimo że żadna z tych wartości pojedynczo niekoniecznie musi taka być.

### games\_openings


| Pole         | Typ     | Dodatkowe informacje           |
|--------------|---------|--------------------------------|
| **`id`**     | INT     | NOT NULL                       |
| `kind`       | VARCHAR | Jeden z (`'service'`, `'pgn'`) |
| `opening_id` | VARCHAR | NULL                           |
| `move_no`    | VARCHAR | NULL                           |

Widok ten łączy wszystkie gry z ich debiutami przy pomocy funkcji. Tam, gdzie wykrycie debiutu jest możliwe (istnieje jakikolwiek wpis w bazie, który można dopasować), tam wartość `opening_id` wskazuje na odpowiedni wpis.
Dodatkowo, kolumna `move_no` trzyma informację o tym, w którym ruchu dany debiut został wykryty.

### games\_rankings

-- Table mapping games from service_games to the rankings in which the game is rated
CREATE VIEW games_rankings AS
SELECT
"service_games"."id" AS "game_id",
"rankings"."id" AS "ranking_id"
FROM "service_games"
INNER JOIN "service_accounts" white_accounts ON (
white_accounts."service_id" = "service_games"."service_id" AND
white_accounts."user_id_in_service" = "service_games"."white_player"
)
INNER JOIN "service_accounts" black_accounts ON (
black_accounts."service_id" = "service_games"."service_id" AND
black_accounts."user_id_in_service" = "service_games"."black_player"
)
CROSS JOIN "rankings"
WHERE
"service_games"."is_ranked" AND
verify_time_control(
"service_games"."clock",
"rankings"."playtime_min",
"rankings"."playtime_max",
"rankings"."extra_move_multiplier"
) AND
(
"rankings"."include_bots" OR
(white_accounts."is_bot" = FALSE AND black_accounts."is_bot" = FALSE)
)
;

### current\_ranking

CREATE VIEW current_ranking AS(
SELECT *
FROM ranking_at_timestamp(CURRENT_TIMESTAMP)
);

### tournaments\_reqs

| Pole             | Typ     | Dodatkowe informacje                                             |
|------------------|---------|------------------------------------------------------------------|
| `tournament_id`  | INT     | NOT NULL                                                         |
| `ranking_type`   | INT     | NOT NULL                                                         |
| `game_count`     | INT     | NULL (puste tylko dla wymagań z `tournaments_ranking_reqs`)      |
| `required_value` | NUMERIC | NULL (puste tylko dla wymagań z `tournaments_ranked_games_reqs`) |

Widok ten łączy dane z `tournaments_ranking_reqs` oraz `tournaments_ranked_games_reqs` i trzyma wszystkie wymagania do dołączenia do turniejów.

### swiss\_tournaments\_players\_points

| Pole                 | Typ     | Dodatkowe informacje |
|----------------------|---------|----------------------|
| `tournament_id`      | INT     | NOT NULL             |
| `user_id_in_service` | VARCHAR | NOT NULL             |
| `round`              | INT     | NOT NULL             |
| `points`             | NUMERIC | NOT NULL             |
| `performance_rating` | NUMERIC | NOT NULL             |

Kolumna `tournament_id` wskazuje na turniej, a kolumna `user_id_in_service` na id gracza w `service_accounts`, który był zarejestrowany w `tournaments_players` w tym turnieju.

Widok ten zawiera informację o punktacji i `performance_rating` dla każdego turnieju i zarejestrowanej rundy.
Zapytanie o krotki z danego turnieju i rundy ujawnia liczby punktów oraz rankingi turniejowe użytkowników w nim zarejestrowanych.

`points` to liczba punktów będąca wielokrotnością `0.5`, zliczająca punkty uzyskane przez gracza nie później niż dana runda.

[Performance rating](https://en.wikipedia.org/wiki/Performance_rating_(chess)) to wartość używana do rozstrzygania remisów - opisuje ona estymowany ranking w trakcie turnieju na podstawie rankingów przeciwników.

### swiss\_tournaments\_round\_standings

| Pole                 | Typ     | Dodatkowe informacje |
|----------------------|---------|----------------------|
| 'place`              | INT     | NOT NULL             |
| `points`             | NUMERIC | NOT NULL             |
| `performance_rating` | NUMERIC | NOT NULL             |
| `user_id_in_service` | VARCHAR | NOT NULL             |
| `tournament_id`      | NUMERIC | NOT NULL             |
| `round`              | INT     | NOT NULL             |

Widok ten łączy informacje z poprzedniego widoku `swiss_tournaments_players_points` i dodaje pole `place`, które dla danego zawodnika, rundy i turnieju zapamiętuje jego miejsce.
