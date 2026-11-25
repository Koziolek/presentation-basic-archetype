#set( $h = '##' )
# ${artifactId}

[English version](README.md)

Aplikacja konsolowa w Javie 27 wygenerowana z archetypu `pl.koziolekweb:presentation-basic-archetype`.

${h} Stos

| Obszar | Narzędzie |
|---|---|
| Obsługa konsoli | [picocli](https://picocli.info) |
| Programowanie funkcyjne | Vavr 1.1.0 |
| Narzędzia pomocnicze | Google Guava, Lombok |
| Testy | JUnit 6, AssertJ, ArchUnit |
| Benchmarki | JMH (profil `benchmarks`) |
| Jakość | Checkstyle, PMD (+ CPD), JaCoCo, Sonar (profil `sonar-local` z `settings.xml`) |

${h} Budowanie

Wymagane: JDK 27 i Maven 3.9+.

```bash
mvn verify                      # testy + Checkstyle + PMD + JaCoCo (próg pokrycia)
java -jar target/${artifactId}.jar --help
java -jar target/${artifactId}.jar greet Ada Linus --upper
```

Raport pokrycia: `target/site/jacoco/index.html`.
Konfiguracja reguł: `config/checkstyle/checkstyle.xml`, `config/pmd/ruleset.xml`.

${h} Benchmarki JMH

Kod benchmarków znajduje się w `src/jmh/java` i jest kompilowany tylko w profilu `benchmarks`
(w IDE aktywuj ten profil, aby zobaczyć źródła).

```bash
mvn verify -Pbenchmarks
mvn verify -Pbenchmarks -Djmh.include=Greeter   # wybór benchmarków (regex)
```

Wyniki trafiają do `target/jmh-result.json`.

${h} Sonar

Analiza Sonar jest podpięta do fazy `verify`, ale domyślnie pomijana (`sonar.verify.skip=true`).
Adres serwera, token i `sonar.verify.skip=false` pochodzą z profilu `sonar-local` w `~/.m2/settings.xml`,
więc na skonfigurowanej maszynie wystarczy:

```bash
mvn verify -Psonar-local
```

${h} Katalog `presentation`

Katalog `presentation/` w głównym katalogu projektu przechowuje materiały prezentacji.
#if ($includeWebServer.equalsIgnoreCase("Y"))

${h} Serwer WWW

Polecenie `serve` uruchamia prosty serwer statyczny (JDK `SimpleFileServer`, tylko interfejs loopback)
i wyświetla pliki z katalogu `presentation`:

```bash
java -jar target/${artifactId}.jar serve                 # http://localhost:8080/
java -jar target/${artifactId}.jar serve --port 9000 --dir presentation
```
#end
