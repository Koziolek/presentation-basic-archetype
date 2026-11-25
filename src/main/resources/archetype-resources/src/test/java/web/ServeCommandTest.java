#if ($includeWebServer.equalsIgnoreCase("Y"))##
package ${package}.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import picocli.CommandLine.ExitCode;

class ServeCommandTest {

    @TempDir
    private Path root;

    @Test
    void startsServerAndPrintsAddress() throws Exception {
        Files.writeString(root.resolve("index.html"), "hello");
        final AtomicReference<String> body = new AtomicReference<>();
        final ServeCommand command = new ServeCommand(server -> body.set(fetch(server)));
        final StringWriter out = new StringWriter();
        final CommandLine commandLine = new CommandLine(command);
        commandLine.setOut(new PrintWriter(out));

        final int exitCode = commandLine.execute("--port", "0", "--dir", root.toString());

        assertThat(exitCode).isEqualTo(ExitCode.OK);
        assertThat(out.toString()).contains("Serving").contains("http://localhost:");
        assertThat(body.get()).isEqualTo("hello");
    }

    @Test
    void failsForMissingDirectory() {
        final CommandLine commandLine = new CommandLine(new ServeCommand(server -> { }));
        commandLine.setErr(new PrintWriter(new StringWriter()));

        final int exitCode = commandLine.execute("--port", "0", "--dir", root.resolve("nope").toString());

        assertThat(exitCode).isEqualTo(ExitCode.SOFTWARE);
    }

    private static String fetch(final PresentationServer server) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            final HttpRequest request = HttpRequest.newBuilder(
                    URI.create("http://localhost:" + server.port() + "/index.html")).build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
#end