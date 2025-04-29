# Tematyka i cele projektu

Zakres planowanej funkcjonalności zmienił się delikatnie od początkowej deklaracji, przesyłamy więc tutaj aktualny plan. Nasza baza jest zaprojektowana tak, aby wspierać w pełni wszystkie funkcjonalności podstawowe oraz rozszerzone.


# Funkcjonalność podstawowa 

- Rozgrywanie partii w aplikacji z botem
  - Weryfikacja legalności ruchów graczy
  - Integracja z istniejącymi silnikami szachowymi (np. [Stockfish](https://stockfishchess.org/))

- Historia rozegranych partii, włączając:
  - Partie zaimportowane w formacie [PGN – Portable Game Notation](https://pl.wikipedia.org/wiki/Portable_Game_Notation)
  - Partie automatycznie pobierane z połączonych kont w innych serwisach szachowych udostępniających API takich jak [chess.com](https://www.chess.com/news/view/published-data-api) lub [lichess.org](https://lichess.org/api)
  - Partie rozegrane w naszym serwisie

- Analiza partii
  - Rozpoznawanie debiutów
  - Wyświetlanie ruchów w [notacji algebraicznej](https://pl.wikipedia.org/wiki/Szachowa_notacja_algebraiczna)


# Funkcjonalność rozszerzona

- System kont
  - Logowanie hasłem
  - Łączenie kont w innych portalach szachowych

- Architektura klient-serwer

- Możliwość przeprowadzenia rozgrywek na żywo z innymi graczami 

- Dodatkowa funkcjonalność analizowania partii
  - Szacowanie rankingu Elo (wyłącznie z partii rozegranych w naszym serwisie)
  - Sugestie lepszych ruchów przez bota

# Schemat bazy

## Tabele

### openings

| Pole     | Typ          | Dodatkowe informacje |
| -------- | ------------ | -------------------- |
| **`id`** | SERIAL       | **PRIMARY KEY**      |
| `eco`    | CHAR(3)      | NOT NULL             |
| `name`   | VARCHAR(256) | NOT NULL             |
| `epd`    | VARCHAR      | UNIQUE NOT NULL      |

Tabela openings przechowuje debiuty które będą rozpoznawane dla gier poprzez `games_openings`. Planujemy oprzeć ją na <https://github.com/lichess-org/chess-openings> lub podobnym zasobie zbierającym debiuty. Kolumna eco to kod debiutu w [Encyklopedii otwarć szachowych](https://pl.wikipedia.org/wiki/Encyklopedia_otwar%C4%87_szachowych), a [epd](https://www.chessprogramming.org/Extended_Position_Description) to format zapisu pozycji na szachownicy.


### users

| Pole            | Typ     | Dodatkowe informacje  |
| --------------- | ------- | --------------------- |
| **`id`**        | SERIAL  | **PRIMARY KEY**       |
| `email`         | VARCHAR | UNIQUE NOT NULL       |
| `password_hash` | VARCHAR | NOT NULL              |
| `elo`           | NUMERIC | NOT NULL DEFAULT 1500 |

Tabela users przechowuje informacje o użytkownikach w systemie kont naszego projektu. Jeżeli nie udałoby nam się zaimplementować systemu kont (jest to funkcjonalność rozszerzona) to działalibyśmy cały czas na jednym użytkowniku domyślnym.

Wstępnie w kolumnie `password_hash` planujemy przechowywać hash w formacie [PHC](https://github.com/P-H-C/phc-string-format/blob/master/phc-sf-spec.md), używając algorytmu hashowania [argon2](https://en.wikipedia.org/wiki/Argon2). Tabela ta ma też oczywiście ograniczenie na poprawność adresu e-mail (pochodzące ze strony [emailregex.com](http://emailregex.com)).

Najciekawszym elementem tej tabeli jest kolumna `elo`. Jest to redundancja, ponieważ elo może być całkowicie wyliczone z rozgrywek gracza przechowywanych w tabeli `service_games`. Obliczanie elo jest jednak bardzo czasochłonne i zdecydowaliśmy, że przeliczanie go za każdym razem gdy chcemy je odczytać byłoby zbyt kosztowne, a liczenie go za pomocą np. materialized view w SQLu byłoby bardzo skomplikowane do zaimplementowania. Planujemy więc po każdej rozgrywce w naszej aplikacji przeliczać elo i zapisywać wynik w bazie.


### game\_services

| Pole     | Typ          | Dodatkowe informacje |
| -------- | ------------ | -------------------- |
| **`id`** | SERIAL       | **PRIMARY KEY**      |
| `name`   | VARCHAR(256) | UNIQUE NOT NULL      |

Tabela `game_services` przechowuje identyfikator dla każdego serwisu szachowego z którym planujemy integrację. Dodatkowo, w tabeli, pod id `1` znajduje się serwis o nazwie `Random Chess`. Jest to serwis odpowiadający partiom rozegranym w naszym serwisie. Traktujemy je tak samo jak partie rozegrane w zewnętrznych serwisach, a więc będziemy je przechowywać w tej samej tabeli `service_games`.


### service\_accounts

| Pole                     | Typ          | Dodatkowe informacje                    |
| ------------------------ | ------------ | --------------------------------------- |
| `user_id`                | INT          | REFERENCES users(id) ON DELETE SET NULL |
| **`service_id`**         | INT          | NOT NULL REFERENCES game\_services(id)  |
| **`user_id_in_service`** | VARCHAR      | NOT NULL                                |
| `display_name`           | VARCHAR(256) | NOT NULL                                |
| `is_bot`                 | BOOL         | NOT NULL                                |

Para **`(service_id, user_id_in_service)`** tworzy klucz podstawowy. Każdemu użytkownikowi serwisu szachowego może odpowiadać co najwyżej jeden użytkownik w naszym systemie kont.

Boty, zarówno w naszym serwisie, jak i serwisach zewnętrznych, posiadają `service_account`, ale z `user_id IS NULL` i `is_bot = TRUE`. 

`service_account` istnieje dla każdego użytkownika który połączył swoje konto w naszym serwisie z dowolnym serwisem szachowym, ale też dla użytkowników którzy nie mają kont w naszym serwisie, a są dowolną ze stron gry przechowywanej w `service_games`. Podjęliśmy tą decyzję, ponieważ jeśli użytkownik taki później utworzy konto w naszym serwisie, to nie chcemy musieć dodawać tej samej gry drugi raz do `service_games` ani modyfikować gier w `service_games`. Zamiast tego, po podłączeniu konta jego `user_id` zostanie po prostu podłączone do już istniejącego `service_account` i będziemy musieli wyłącznie pobrać brakujące gry z odpowiedniego API do `service_games`.

Konsekwencją tej decyzji jest to, że nie chcemy nigdy usuwać `service_account` i zamiast tego po usunięciu użytkownika odłączamy od niego wszystkie jego `service_accounts`.
Jest to realizowane poprzez ustawienie `ON DELETE SET NULL` w polu `user_id`.

Dodatkowo, każdy użytkownik naszego serwisu posiada dokładnie jedno odpowiadające konto w tabeli `service_accounts` o `service_id` = `1` (id naszego serwisu szachowego). Konto to przechowuje jego nazwę użytkownika w naszym serwisie, a jego `user_id_in_service` = `user_id`.
Jest to redundancja, ale:
1. ponieważ zdecydowaliśmy się na klucz podstawowy `(service_id, user_id_in_service)`, `user_id_in_service` musi być różne dla każdego użytkownika,
2. po usunięciu konta użytkownika jego odpowiadający `service_account` musi dalej istnieć (aby gry rozegrane z nim nie zniknęły). Te różne `service_accounts` w naszym serwisie pozostałe po usuniętych użytkownikach muszą być w jakiś sposób rozróżnialne i tym sposobem jest właśnie `user_id_in_service`.

Wyzwalacze `add_default_service_to_user`, `prevent_default_service_modification` oraz `prevent_default_service_deletion` służą upewnieniu się, że od utworzenia użytkownika po jego usunięcie jego odpowiadające konto w `service_accounts` będzie zawsze istnieć.

Dodatkowo, ograniczenie `valid_system_account` w `service_accounts` upewnia się, że dla kont tych spełnione są powyższe założenia:
- dla użytkowników póki ich konto istnieje, to `user_id_in_service = user_id`,
- dla botów `user_id IS NULL`.


### Tabele service\_games i pgn\_games

Te tabele przechowują partie szachowe, które będą analizowane w naszej aplikacji. Tabela `service_games` przechowuje zsynchronizowane partie z zewnętrznych serwisów oraz partie rozegrane w naszym serwisie. Tabela `pgn_games` przechowuje partie, które zostały zaimportowane ręcznie przez użytkownika.

W późniejszej sekcji <!-- TODO: Add heading link --> opisaliśmy dlaczego zdecydowaliśmy się zamodelować partie szachowe właśnie w ten sposób.

Klucze podstawowe **`id`** w `service_games` i `pgn_games` mogą się powtarzać.


#### Wspólne pola w tabelach service\_games i pgn\_games

| Pole       | Typ       | Dodatkowe informacje |
| ---------- | --------- | -------------------- |
| `moves`    | VARCHAR   | NOT NULL             |
| `date`     | TIMESTAMP |                      |
| `metadata` | JSONB     |                      |

Kolumna moves przechowuje ruchy graczy w partii w postaci [PGN](https://pl.wikipedia.org/wiki/Portable_Game_Notation), bez metadanych.

Kolumna metadata zawiera wszystkie niestandardowe pola metadanych pochodzących z opisu partii w postaci PGN. Dane przechowujemy w formacie JSON, choć nie spełnia to reguły atomowości, bo dokładny ich format może się różnić w zależności od serwisu, a dane te służą jedynie do wyświetlenia użytkownikowi i ponownego eksportu rozgrywki do formatu PGN, nigdy nie będziemy robić zapytań dotyczących metadanych w tym polu.


#### Pola występujące tylko w service\_games

| Pole                 | Typ     | Dodatkowe informacje |
| -------------------- | ------- | -------------------- |
| **`id`**             | SERIAL  | **PRIMARY KEY**      |
| `game_id_in_service` | VARCHAR | NULL                 |
| `service_id`         | INT     | NOT NULL             |
| `white_player`       | VARCHAR | NOT NULL             |
| `black_player`       | VARCHAR | NOT NULL             |

`game_id_in_service` to ID pochodzące z zewnętrznego API. Na pary `(game_id_in_service, service_id)` jest założone ograniczenie UNIQUE.

Dla zewnętrznych serwisów `white_player` i `black_player` oznaczają id użytkownika w API tego serwisu. Pary `(white_player, service_id)` i `(black_player, service_id)` są kluczami obcymi wskazującymi na pary `(service_id, user_id_in_service)`, czyli klucz podstawowy, w tabeli `service_accounts`.


#### Pola występujące tylko w pgn\_games

| Pole                | Typ     | Dodatkowe informacje                                 |
| ------------------- | ------- | ---------------------------------------------------- |
| **`id`**            | SERIAL  | **PRIMARY KEY**                                      |
| `owner_id`          | INT     | NOT NULL<br> REFERENCES "users" ("id")<br> ON DELETE CASCADE |
| `white_player_name` | VARCHAR |                                                      |
| `black_player_name` | VARCHAR |                                                      |

## Widoki

### games

| Pole     | Typ       | Dodatkowe informacje           |
| -------- | --------- | ------------------------------ |
| `id`       | INT       | NOT NULL                       |
| `kind`     | VARCHAR   | Jeden z (`'service'`, `'pgn'`) |
| `moves`    | VARCHAR   | NOT NULL                       |
| `date`     | timestamp |                                |
| `metadata` | JSONB     |                                |

Widok games jest UNION `service_games` i `pgn_games`. `kind` jest równy `'service'` dla gier pochodzących z `service_games` i `'pgn'` dla gier pochodzących z `'pgn_games'`. `id` nie jest unikatowe dla wszystkich jego elementów, ale para `(id, kind)` już jest. 

### games\_openings

| Pole         | Typ     | Dodatkowe informacje           |
| ------------ | ------- | ------------------------------ |
| `game_id`    | INT     | NOT NULL                       |
| `game_kind`  | VARCHAR | Jeden z (`'service'`, `'pgn'`) |
| `opening_id` | INT     |                                |

Widok `games_openings` jest planowanym widokiem łączącym gry w widoku games z ich debiutami. Planujemy zaimplementować go pisząc funkcję która porównuje kolejne elementy tabeli `epd_positions` dla danej gry z kolumną epd tabeli openings, znajdując ostatnią pozycję której może zostać przypisany debiut i zapisując go w `opening_id`. Implementacja tego widoku była zbyt skomplikowana na pierwszy etap projektu, dlatego planujemy to zrobić w etapie drugim.

### users\_games

```sql
CREATE VIEW "users_games" AS (
    SELECT users."id" as "user_id", sg."id" as "game_id", 'service' AS "kind", "moves", "date", "metadata"
```

| Pole       | Type      | Dodatkowe informacje           |
| ---------- | --------- | ------------------------------ |
| `user_id`  | INT       | NOT NULL                       |
| `game_id`  | INT       | NOT NULL                       |
| `kind`     | VARCHAR   | Jeden z (`'service'`, `'pgn'`) |
| `moves`    | VARCHAR   | NOT NULL                       |
| `date`     | TIMESTAMP |                                |
| `metadata` | JSONB     |                                |

Pola `moves`, `date` i `metadata` to wspólne pola tabel `pgn_games` i `service_games`.

1. Jedna tabela games z kolumnami obu typów i checkami weryfikującymi, że kolumny jednego typu są ustawione na wartości inne niż NULL, a kolumny drugiego typu wypełnione są NULLami. Wady: duża ilość nulli w każdym wierszu, duża redundencja: NOT NULL w jednej sekcji znaczy że cała druga sekcja jest NULL

2. Tabela games ze wspólnymi kolumnami oraz tabele service\_games i pgn\_games. Tabela games posiada pola z kluczami obcymi do service\_games.id i pgn\_games.id. Wady: możliwość powstania sieroty w service\_games lub pgn\_games (a więc np pgn\_game która ma właściciela a nie faktyczną rozgrywkę)

3. Tabela games ze wspólnymi kolumnami oraz tabele service\_games i pgn\_games. Tabele service\_games i pgn\_games posiadają pola game\_id będące kluczami obcymi do games.id\
   Wady: możliwość posiadania sieroty w games, lub gry która jest jednocześnie pgn\_games i service\_games.

4. Tabela games ze wspólnymi kolumnami oraz tabele service\_games i pgn\_games dziedziczące od games. Wady: klucze obce z innych tabel nie mogą wskazywać na games. id w games nie jest jednoznaczne - może być service\_game i pgn\_game mające to samo id (możliwe do rozwiązania z dużą liczbą triggerów). Powoduje to problemy w funkcjonowaniu games\_openings i możliwe większe problemy z rozszerzaniem bazy. Rozwiązanie byłoby praktycznie idealne gdyby dziedziczenie dziedziczyło też klucze obce i inne ograniczenia.

5. Tabela games ze wspólnymi kolumnami oraz tabele service\_games i pgn\_games. Tabela games posiada pola z kluczami obcymi do service\_games.id i pgn\_games.id. Service\_games.id i pgn\_games.id są symetrycznymi kluczami obcymi wskazującymi na games. Wady: duplikacja kluczy obcych, możliwość desynchronizacji bez dużej liczby triggerów (np. dany service\_game wskazuje na jakiś game, a ten game na inny service\_game lub pgn\_game) 

6. Tabela games ze wspólnymi kolumnami oraz tabele service\_games i pgn\_games. Dodatkowa tabela link, przechowująca obowiązkowy kucz obcy do id w games, i klucze obce do service\_games i pgn\_games, dokładnie jeden z których jest not null. Dodatkowo, id w games, pgn\_games i service\_games jest kluczem obcym do odpowiadających kolumn w link. Wady: rozwiązanie to usuwa możliwość desynchronizacji poprzedniego rozwiązania, ale utrzymuje duplikację kluczy obcych i dodaje całą niepotrzebną tabelę.

7. Tabela games ze wspólnymi kolumnami oraz tabele service\_games i pgn\_games. Dodatkowe pole game\_type we wszystkich trzech kolumnach - GENERATED ALWAYS AS(‘pgn’) w pgn, analogicznie w service, w games pokazujące typ gry. Para (id, type) będąca foreign key z service\_games i pgn\_games w games. Wady: konieczność stworzenia dodatkowych kolumn GENERATED w service\_games i pgn\_games, możliwość stworzenia sierot w games (choć sieroty w games są mniej problematyczne, bo raczej nigdy nie odwołujemy sie do games bezpośrednio).

8. Finalne rozwiązanie: tabele games i service\_games duplikujące wspólne informacje które wcześniej były w games. W razie potrzeby możliwość robienia UNION na tabelach. Wady: dane o grach są przechowywane osobno, w dwóch różnych tabelach, przez co np. games\_openings musi odwoływać sie do UNION service, pgn, a zbiór gier danego gracza nie jest już podzbiorem jednej tabeli
