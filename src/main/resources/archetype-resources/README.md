#set( $h = '##' )
# ${artifactId}

[Wersja polska](README_pl.md)

Java 27 console application generated from the `pl.koziolekweb:presentation-basic-archetype` archetype.

${h} Stack

| Area | Tool |
|---|---|
| Console handling | [picocli](https://picocli.info) |
| Functional programming | Vavr 1.1.0 |
| Utilities | Google Guava, Lombok |
| Tests | JUnit 6, AssertJ, ArchUnit |
| Benchmarks | JMH (`benchmarks` profile) |
| Quality | Checkstyle, PMD (+ CPD), JaCoCo, Sonar (`sonar-local` profile from `settings.xml`) |

${h} Building

Requires JDK 27 and Maven 3.9+.

```bash
mvn verify                      # tests + Checkstyle + PMD + JaCoCo (coverage thresholds)
java -jar target/${artifactId}.jar --help
java -jar target/${artifactId}.jar greet Ada Linus --upper
```

Coverage report: `target/site/jacoco/index.html`.
Rule configuration: `config/checkstyle/checkstyle.xml`, `config/pmd/ruleset.xml`.

${h} JMH benchmarks

Benchmark sources live in `src/jmh/java` and are compiled only in the `benchmarks` profile
(activate the profile in your IDE to see the sources).

```bash
mvn verify -Pbenchmarks
mvn verify -Pbenchmarks -Djmh.include=Greeter   # select benchmarks (regex)
```

Results are written to `target/jmh-result.json`.

${h} Sonar

The Sonar analysis is bound to the `verify` phase but skipped by default (`sonar.verify.skip=true`).
The server URL, the token and `sonar.verify.skip=false` come from the `sonar-local` profile in `~/.m2/settings.xml`,
so on a configured machine this is enough:

```bash
mvn verify -Psonar-local
```

${h} The `presentation` directory

The `presentation/` directory in the project root holds the presentation materials.
#if ($includeWebServer.equalsIgnoreCase("Y"))

${h} Web server

The `serve` command starts a simple static web server (JDK `SimpleFileServer`, loopback interface only)
that serves the files from the `presentation` directory:

```bash
java -jar target/${artifactId}.jar serve                 # http://localhost:8080/
java -jar target/${artifactId}.jar serve --port 9000 --dir presentation
```
#end