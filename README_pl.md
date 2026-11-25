# presentation-basic-archetype

[English version](README.md)

Archetyp Mavena `pl.koziolekweb:presentation-basic-archetype` dla aplikacji konsolowych w Javie 27.

## Co generuje

- Java 27, pakowanie do wykonywalnego jara (`maven-shade-plugin`), `maven-enforcer-plugin` pilnuje JDK 27+ i Mavena 3.9+
- Obsługa konsoli: **picocli** (polecenia `greet` i opcjonalnie `serve`)
- Biblioteki: **Vavr 1.1.0**, **Google Guava**, **Lombok** (zakres `provided`, procesor w `annotationProcessorPaths`, `lombok.config`)
- Testy: **JUnit 6**, **AssertJ**, **ArchUnit**
- Benchmarki: **JMH** w profilu `benchmarks` (kod w `src/jmh/java`)
- Jakość: **Checkstyle**, **PMD** (+ CPD), **JaCoCo** (progi pokrycia w `mvn verify`), **Sonar** (wiązany do `verify`, uruchamiany przez profil `sonar-local` z `~/.m2/settings.xml`; w pomie nie ma adresu serwera ani tokenu)
- `.sdkmanrc` (SDKMAN: Java 27.0.1.1-open, Maven 3.10.2, mvnd) oraz `.mvn/devtools-extension.properties`, które z wersji Javy
  w `.sdkmanrc` wyliczają `maven.compiler.release` (`sdkman.java.version`)
- Core extension `devtools-maven-extension` (`.mvn/extensions.xml`)
- Katalog `presentation/` w głównym katalogu projektu
- Opcjonalny serwer WWW (JDK `SimpleFileServer`, tylko loopback), który wyświetla pliki z `presentation/`

## Użycie

Interaktywnie (archetyp zapyta m.in. o `includeWebServer`):

```bash
mvn archetype:generate \
  -DarchetypeGroupId=pl.koziolekweb \
  -DarchetypeArtifactId=presentation-basic-archetype \
  -DarchetypeVersion=1.0.0
```

Nieinteraktywnie:

```bash
mvn archetype:generate -B \
  -DarchetypeGroupId=pl.koziolekweb \
  -DarchetypeArtifactId=presentation-basic-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=com.example -DartifactId=demo -Dversion=1.0.0-SNAPSHOT -Dpackage=com.example.demo \
  -DincludeWebServer=Y
```

Pytanie o serwer WWW to wybór Y/N (`includeWebServer`, domyślnie `Y`, wielkość liter nie ma znaczenia).
Przy `N` kod serwera nie trafia do projektu, a `RootCommand` nie rejestruje polecenia `serve`.

Ograniczenie: pliki opcjonalne są opakowane w warunek Velocity, a plugin archetypu tworzy je wtedy jako
**puste (0 B) pliki** `.java` w pakiecie `web` (4 pliki: 2 w `src/main`, 2 w `src/test`). Kompilują się bez problemu,
ale można je usunąć. Skrypt `archetype-post-generate.groovy`, który mógłby je skasować, nie działa pod JDK 27
(Groovy z pluginu nie czyta klas w formacie 71), więc z niego zrezygnowano.

## Budowanie archetypu

Testy integracyjne archetypu (`src/test/resources/projects/*`) generują oba warianty projektu
i uruchamiają na nich `mvn verify`, więc **do zbudowania archetypu potrzebny jest JDK 27**.

```bash
mvn install                       # budowa + testy integracyjne
mvn install -Darchetype.test.skip # sama paczka archetypu, bez generowania projektów testowych
```

## Wersjonowanie i publikacja w Maven Central

Skonfigurowane w `pom.xml`:

- `versions-maven-plugin` (reguły w `versions-rules.xml` ignorują wersje alpha/beta/RC):
  `mvn versions:set -DnewVersion=1.0.0`, `mvn versions:display-dependency-updates`, `mvn versions:display-plugin-updates`
- `maven-release-plugin` (tag `v<wersja>`, profil `deployment`): `mvn release:prepare release:perform`
- profil `deployment`: jar ze źródłami, jar `javadoc` (archetyp nie ma API, więc zawiera tylko notkę),
  podpis GPG (`maven-gpg-plugin`) i `central-publishing-maven-plugin` (Central Portal)

Wymagania do publikacji: wpis `<server><id>central</id>` z tokenem Central Portal w `~/.m2/settings.xml`,
klucz GPG (`gpg.keyname` lub gpg-agent) oraz zweryfikowana przestrzeń nazw `pl.koziolekweb` w Central Portal.

```bash
mvn -Pdeployment deploy                          # wysyła paczkę, publikacja ręcznie w portalu
mvn -Pdeployment deploy -Dcentral.autoPublish=true
```

`./release.sh` automatyzuje całe wydanie: usuwa `-SNAPSHOT` (`versions:set`), commituje `Release X`, uruchamia
`mvn clean deploy -Pdeployment` z `-Dcentral.autoPublish=true -Dcentral.waitUntil=published` (czeka, aż Central zgłosi
PUBLISHED), taguje `vX` i podnosi wersję do kolejnego `-SNAPSHOT`. Opcje: `-y` (bez pytania), `--push` (wypchnięcie
gałęzi i tagu do origin), `--check-published` (wznowienie po deployu, który zgłosił błąd mimo publikacji w Central).
Wymaga czystego drzewa roboczego i JDK 27. `maven-release-plugin` też jest skonfigurowany, ale docelowo służy `release.sh`.

## Licencja

Apache License 2.0 (patrz `LICENSE`).

