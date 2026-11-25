# presentation-basic-archetype

[Wersja polska](README_pl.md)

Maven archetype `pl.koziolekweb:presentation-basic-archetype` for Java 27 console applications.

## What it generates

- Java 27, executable fat jar (`maven-shade-plugin`); `maven-enforcer-plugin` requires JDK 27+ and Maven 3.9+
- Console handling: **picocli** (commands `greet` and, optionally, `serve`)
- Libraries: **Vavr 1.1.0**, **Google Guava**, **Lombok** (`provided` scope, processor in `annotationProcessorPaths`, `lombok.config`)
- Tests: **JUnit 6**, **AssertJ**, **ArchUnit**
- Benchmarks: **JMH** in the `benchmarks` profile (sources in `src/jmh/java`)
- Quality: **Checkstyle**, **PMD** (+ CPD), **JaCoCo** (coverage thresholds in `mvn verify`), **Sonar** (bound to `verify`, enabled by the `sonar-local` profile from `~/.m2/settings.xml`; the pom contains neither a server URL nor a token)
- `.sdkmanrc` (SDKMAN: Java 27.0.1.1-open, Maven 3.10.2, mvnd) and `.mvn/devtools-extension.properties`, which derive
  `maven.compiler.release` (`sdkman.java.version`) from the Java version in `.sdkmanrc`
- Core extension `devtools-maven-extension` (`.mvn/extensions.xml`)
- A `presentation/` directory in the project root
- An optional web server (JDK `SimpleFileServer`, loopback only) that serves the files from `presentation/`

## Usage

Interactive (the archetype asks, among others, for `includeWebServer`):

```bash
mvn archetype:generate \
  -DarchetypeGroupId=pl.koziolekweb \
  -DarchetypeArtifactId=presentation-basic-archetype \
  -DarchetypeVersion=1.0.0
```

Non-interactive:

```bash
mvn archetype:generate -B \
  -DarchetypeGroupId=pl.koziolekweb \
  -DarchetypeArtifactId=presentation-basic-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=com.example -DartifactId=demo -Dversion=1.0.0-SNAPSHOT -Dpackage=com.example.demo \
  -DincludeWebServer=Y
```

The web server question is a Y/N choice (`includeWebServer`, default `Y`, case-insensitive).
With `N` the server code is not generated and `RootCommand` does not register the `serve` command.

Limitation: the optional files are wrapped in a Velocity condition, and the archetype plugin then creates them as
**empty (0 B)** `.java` files in the `web` package (4 files: 2 in `src/main`, 2 in `src/test`). They compile fine
and can be deleted. An `archetype-post-generate.groovy` script that could remove them does not work on JDK 27
(the Groovy bundled with the plugin cannot read class file version 71), so it was dropped.

## Building the archetype

The archetype's integration tests (`src/test/resources/projects/*`) generate both project variants and run
`mvn verify` on them, so **JDK 27 is required to build the archetype**.

```bash
mvn install                       # build + integration tests
mvn install -Darchetype.test.skip # archetype package only, without generating test projects
```

## Versioning and publishing to Maven Central

Configured in `pom.xml`:

- `versions-maven-plugin` (rules in `versions-rules.xml` ignore alpha/beta/RC versions):
  `mvn versions:set -DnewVersion=1.0.0`, `mvn versions:display-dependency-updates`, `mvn versions:display-plugin-updates`
- `maven-release-plugin` (tag `v<version>`, profile `deployment`): `mvn release:prepare release:perform`
- `deployment` profile: sources jar, `javadoc` jar (an archetype has no API, so it only contains a note),
  GPG signing (`maven-gpg-plugin`) and `central-publishing-maven-plugin` (Central Portal)

Publishing requires: a `<server><id>central</id>` entry with a Central Portal token in `~/.m2/settings.xml`,
a GPG key (`gpg.keyname` or gpg-agent) and a verified `pl.koziolekweb` namespace in the Central Portal.

```bash
mvn -Pdeployment deploy                          # uploads the bundle, publish manually in the portal
mvn -Pdeployment deploy -Dcentral.autoPublish=true
```

`./release.sh` automates a whole release: it removes `-SNAPSHOT` (`versions:set`), commits `Release X`, runs
`mvn clean deploy -Pdeployment` with `-Dcentral.autoPublish=true -Dcentral.waitUntil=published` (waits until Central
reports PUBLISHED), tags `vX` and bumps to the next `-SNAPSHOT`. Options: `-y` (no question), `--push` (push branch and
tag to origin), `--check-published` (resume after a deploy that failed although Central published). It needs a clean
working tree and JDK 27. `maven-release-plugin` is configured too, but `release.sh` is the intended way.

## License

Apache License 2.0 (see `LICENSE`).Je
