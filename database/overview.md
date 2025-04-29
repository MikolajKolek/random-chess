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

## openings

| Pole   | Typ          | Dodatkowe informacje |
| ------ | ------------ | -------------------- |
| **id** | SERIAL       | **PRIMARY KEY**      |
| eco    | CHAR(3)      | NOT NULL             |
| name   | VARCHAR(256) | NOT NULL             |
| epd    | VARCHAR      | UNIQUE NOT NULL      |

Tabela openings przechowuje debiuty które będą rozpoznawane dla gier poprzez games\_openings. Planujemy oprzeć ją na <https://github.com/lichess-org/chess-openings> lub podobnym zasobie zbierającym debiuty. Kolumna eco to kod debiutu w [Encyklopedii otwarć szachowych](https://pl.wikipedia.org/wiki/Encyklopedia_otwar%C4%87_szachowych), a [epd](https://www.chessprogramming.org/Extended_Position_Description) to format zapisu pozycji na szachownicy.


## users

| Pole           | Typ     | Dodatkowe informacje  |
| -------------- | ------- | --------------------- |
| **id**         | SERIAL  | **PRIMARY KEY**       |
| email          | VARCHAR | UNIQUE NOT NULL       |
| password\_hash | VARCHAR | NOT NULL              |
| elo            | NUMERIC | NOT NULL DEFAULT 1500 |

Tabela users przechowuje informacje o użytkownikach w systemie kont naszego projektu. Jeżeli nie udałoby nam się zaimplementować systemu kont (jest to funkcjonalność rozszerzona) to działalibyśmy cały czas na jednym użytkowniku domyślnym.

Wstępnie w kolumnie password\_hash planujemy przechowywać hash w formacie [PHC](https://github.com/P-H-C/phc-string-format/blob/master/phc-sf-spec.md), używając algorytmu hashowania [argon2](https://en.wikipedia.org/wiki/Argon2). Tabela ta ma też oczywiście ograniczenie na poprawność adresu e-mail (pochodzące ze strony [emailregex.com](http://emailregex.com)).

Najciekawszym elementem tej tabeli jest kolumna elo. Jest to redundencja, ponieważ elo może być całkowicie wyliczone z rozgrywek gracza przechowywanych w tabeli service\_games. Obliczanie elo jest jednak bardzo czasochłonne i zdecydowaliśmy, że przeliczanie go za każdym razem gdy chcemy je odczytać byłoby zbyt kosztowne, a liczenie go za pomocą np. materialized view w SQLu byłoby bardzo skomplikowane do zaimplementowania. Planujemy więc po każdej rozgrywce w naszej aplikacji przeliczać elo i zapisywać wynik w bazie.


## game\_services

| Pole   | Typ          | Dodatkowe informacje |
| ------ | ------------ | -------------------- |
| **id** | SERIAL       | **PRIMARY KEY**      |
| name   | VARCHAR(256) | UNIQUE NOT NULL      |

Tabela game\_services przechowuje identyfikator dla każdego serwisu szachowego z którym planujemy integrację. Dodatkowo, w tabeli, pod id 1 znajduje się serwis o nazwie ‘Random Chess’. Jest to serwis odpowiadający partiom rozegranym w naszym serwisie. Traktujemy je tak samo jak partie rozegrane w zewnętrznych serwisach, a więc będziemy je przechowywać w tej samej tabeli service\_games.


## service\_accounts

| Pole                      | Typ          | Dodatkowe informacje                    |
| ------------------------- | ------------ | --------------------------------------- |
| user\_id                  | INT          | REFERENCES users(id) ON DELETE SET NULL |
| **service\_id**           | INT          | NOT NULL REFERENCES game\_services(id)  |
| **user\_id\_in\_service** | VARCHAR      | NOT NULL                                |
| display\_name             | VARCHAR(256) | NOT NULL                                |
| is\_bot                   | BOOL         | NOT NULL                                |

Para (**service\_id**, **user\_id\_in\_service**) tworzy klucz podstawowy. Każdemu użytkownikowi serwisu szachowego może odpowiadać co najwyżej jeden użytkownik w naszym systemie kont.\
\
Boty, zarówno w naszym serwisie, jak i serwisach zewnętrznych, posiadają service\_account, ale z user\_id IS NULL i is\_bot = TRUE. 

Service\_account istnieje dla każdego użytkownika który połączył swoje konto w naszym serwisie z dowolnym serwisem szachowym, ale też dla użytkowników którzy nie mają kont w naszym serwisie, a są dowolną ze stron gry przechowywanej w service\_games. Podjęliśmy tą decyzję, ponieważ jeśli użytkownik taki później utworzy konto w naszym serwisie, to nie chcemy musieć dodawać tej samej gry drugi raz do service\_games ani modyfikować gier w service\_games. Zamiast tego, po podłączeniu konta jego user\_id zostanie po prostu podłączone do już istniejącego service\_account i będziemy musieli wyłącznie pobrać brakujące gry z odpowiedniego API do service\_games.

Konsekwencją tej decyzji jest to, że nie chcemy nigdy usuwać service\_account i zamiast tego po usunięciu użytkownika odłączamy od niego wszystkie jego service\_accounts. Robi to ON DELETE SET NULL w polu user\_id.

Dodatkowo, każdy użytkownik naszego swerwisu posiada dokładnie jedno odpowiadające konto w tabeli service\_accounts o service\_id = 1 (id naszego serwisu szachowego). Konto to przechowuje jego nazwę użytkownika w naszym serwisie, a jego user\_id\_in\_service = user\_id. Jest to redundencja, ale po pierwsze, ponieważ zdecydowaliśmy się na klucz podstawowy (service\_id, user\_id\_in\_service), user\_id\_in\_service musi być różne dla każdego użytkownika, a po drugie po usunięciu konta użytkownika jego odpowiadający service\_account musi dalej istnieć (aby jego gry nie zniknęły). Te różne service\_accounts w naszym serwisie pozostałe po usuniętych użytkownikach muszą być w jakiś sposób rozróżnialne i tym sposobem jest właśnie user\_id\_in\_service.\
\
Wyzwalacze add\_default\_service\_to\_user, prevent\_default\_service\_modification oraz prevent\_default\_service\_deletion służą upewnieniu się, że od utworzenia użytkownika po jego usunięcie jego odpowiadające konto w service\_accounts będzie zawsze istnieć. Dodatkowo, ograniczenie valid\_system\_account w service\_accounts upewnia się, że dla kont tych spełnione są powyższe założenia: dla użytkowników póki ich konto istnieje, user\_id\_in\_service = user\_id, a dla botów user\_id IS NULL.


## Tabele service\_games i pgn\_games

Te tabele przechowują partie szachowe, które będą analizowane w naszej aplikacji. Tabela service\_games przechowuje zsynchronizowane partie z zewnętrznych serwisów oraz partie rozegrane w naszym serwisie. Tabela pgn\_games przechowuje partie, które zostały zaimportowane ręcznie przez użytkownika.

W późniejszej sekcji opisaliśmy dlaczego zdecydowaliśmy się zamodelować partie szachowe właśnie w ten sposób.

Klucze podstawowe **id** w service\_games i pgn\_games mogą się powtarzać.


## Wspólne pola w tabelach service\_games i pgn\_games

| Pole     | Typ       | Dodatkowe informacje |
| -------- | --------- | -------------------- |
| moves    | VARCHAR   | NOT NULL             |
| date     | TIMESTAMP |                      |
| metadata | JSONB     |                      |

Kolumna moves przechowuje ruchy graczy w partii w postaci [PGN](https://pl.wikipedia.org/wiki/Portable_Game_Notation), bez metadanych.

Kolumna metadata zawiera wszystkie niestandardowe pola metadanych pochodzących z opisu partii w postaci PGN. Dane przechowujemy w formacie JSON, choć nie spełnia to reguły atomowości, bo dokładny ich format może się różnić w zależności od serwisu, a dane te służą jedynie do wyświetlenia użytkownikowi i ponownego eksportu rozgrywki do formatu PGN, nigdy nie będziemy robić zapytań dotyczących metadanych w tym polu.


## Pola występujące tylko w service\_games

| Pole                  | Typ     | Dodatkowe informacje |
| --------------------- | ------- | -------------------- |
| **id**                | SERIAL  | **PRIMARY KEY**      |
| game\_id\_in\_service | VARCHAR | NULL                 |
| service\_id           | INT     | NOT NULL             |
| white\_player         | VARCHAR | NOT NULL             |
| black\_player         | VARCHAR | NOT NULL             |

game\_id\_in\_service to ID pochodzące z zewnętrznego API. Na pary (game\_id\_in\_service, service\_id) jest założone ograniczenie UNIQUE.

Dla zewnętrznych serwisów white\_player i black\_player oznaczają id użytkownika w API tego serwisu. Pary (white\_player, service\_id) i (black\_player, service\_id) są kluczami obcymi wskazującymi na pary (service\_id, user\_id\_in\_service), czyli klucz podstawowy, w tabeli service\_accounts.


## Pola występujące tylko w pgn\_games

| Pole                | Typ       | Dodatkowe informacje                                 |
| ------------------- | --------- | ---------------------------------------------------- |
| **id**              | SERIAL    | **PRIMARY KEY**                                      |
| owner\_id           | owner\_id | NOT NULL REFERENCES "users" ("id") ON DELETE CASCADE |
| white\_player\_name | VARCHAR   |                                                      |
| black\_player\_name | VARCHAR   |                                                      |


#

##

## games

| Pole     | Typ       | Dodatkowe informacje       |
| -------- | --------- | -------------------------- |
| id       | INT       | NOT NULL                   |
| kind     | VARCHAR   | Jeden z (‘service’, ‘pgn’) |
| moves    | VARCHAR   | NOT NULL                   |
| date     | timestamp |                            |
| metadata | JSONB     |                            |

Widok games jest UNION service\_games i pgn\_games. Kind jest równy ‘service’ dla gier pochodzących z service\_games i ‘pgn’ dla gier pochodzących z pgn\_games. W związku z tym id nie jest unikatowe dla wszystkich jego elementów, ale para (id, kind) już jest. 

## games\_openings

| Pole        | Typ     | Dodatkowe informacje       |
| ----------- | ------- | -------------------------- |
| game\_id    | INT     | NOT NULL                   |
| game\_kind  | VARCHAR | Jeden z (‘service’, ‘pgn’) |
| opening\_id | INT     |                            |

Widok games\_openings jest planowanym widokiem łączącym gry w widoku games z ich debiutami. Planujemy zaimplementować go pisząc funkcję która porównuje kolejne elementy tablicy epd\_positions dla danej gry z kolumną epd tabeli openings, znajdując ostatnią pozycję której może zostać przypisany debiut i zapisując go w opening\_id. Implementacja tego widoku była zbyt skomplikowana na pierwszy etap projektu, dlatego planujemy to zrobić w etapie drugim.

1. Jedna tabela games z kolumnami obu typów i checkami weryfikującymi, że kolumny jednego typu są ustawione na wartości inne niż NULL, a kolumny drugiego typu wypełnione są NULLami. Wady: duża ilość nulli w każdym wierszu, duża redundencja: NOT NULL w jednej sekcji znaczy że cała druga sekcja jest NULL

2. Tabela games ze wspólnymi kolumnami oraz tabele service\_games i pgn\_games. Tabela games posiada pola z kluczami obcymi do service\_games.id i pgn\_games.id. Wady: możliwość powstania sieroty w service\_games lub pgn\_games (a więc np pgn\_game która ma właściciela a nie faktyczną rozgrywkę)

3. Tabela games ze wspólnymi kolumnami oraz tabele service\_games i pgn\_games. Tabele service\_games i pgn\_games posiadają pola game\_id będące kluczami obcymi do games.id\
   Wady: możliwość posiadania sieroty w games, lub gry która jest jednocześnie pgn\_games i service\_games.

4. Tabela games ze wspólnymi kolumnami oraz tabele service\_games i pgn\_games dziedziczące od games. Wady: klucze obce z innych tabel nie mogą wskazywać na games. id w games nie jest jednoznaczne - może być service\_game i pgn\_game mające to samo id (możliwe do rozwiązania z dużą liczbą triggerów). Powoduje to problemy w funkcjonowaniu games\_openings i możliwe większe problemy z rozszerzaniem bazy. Rozwiązanie byłoby praktycznie idealne gdyby dziedziczenie dziedziczyło też klucze obce i inne ograniczenia.

5. Tabela games ze wspólnymi kolumnami oraz tabele service\_games i pgn\_games. Tabela games posiada pola z kluczami obcymi do service\_games.id i pgn\_games.id. Service\_games.id i pgn\_games.id są symetrycznymi kluczami obcymi wskazującymi na games. Wady: duplikacja kluczy obcych, możliwość desynchronizacji bez dużej liczby triggerów (np. dany service\_game wskazuje na jakiś game, a ten game na inny service\_game lub pgn\_game) 

6. Tabela games ze wspólnymi kolumnami oraz tabele service\_games i pgn\_games. Dodatkowa tabela link, przechowująca obowiązkowy kucz obcy do id w games, i klucze obce do service\_games i pgn\_games, dokładnie jeden z których jest not null. Dodatkowo, id w games, pgn\_games i service\_games jest kluczem obcym do odpowiadających kolumn w link. Wady: rozwiązanie to usuwa możliwość desynchronizacji poprzedniego rozwiązania, ale utrzymuje duplikację kluczy obcych i dodaje całą niepotrzebną tabelę.

7. Tabela games ze wspólnymi kolumnami oraz tabele service\_games i pgn\_games. Dodatkowe pole game\_type we wszystkich trzech kolumnach - GENERATED ALWAYS AS(‘pgn’) w pgn, analogicznie w service, w games pokazujące typ gry. Para (id, type) będąca foreign key z service\_games i pgn\_games w games. Wady: konieczność stworzenia dodatkowych kolumn GENERATED w service\_games i pgn\_games, możliwość stworzenia sierot w games (choć sieroty w games są mniej problematyczne, bo raczej nigdy nie odwołujemy sie do games bezpośrednio).

8. Finalne rozwiązanie: tabele games i service\_games duplikujące wspólne informacje które wcześniej były w games. W razie potrzeby możliwość robienia UNION na tabelach. Wady: dane o grach są przechowywane osobno, w dwóch różnych tabelach, przez co np. games\_openings musi odwoływać sie do UNION service, pgn, a zbiór gier danego gracza nie jest już podzbiorem jednej tabeli
