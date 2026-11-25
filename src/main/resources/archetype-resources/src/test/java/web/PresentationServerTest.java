#if ($includeWebServer.equalsIgnoreCase("Y"))##
package ${package}.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PresentationServerTest {

    @TempDir
    private Path root;

    @Test
    void servesFilesFromDirectory() throws Exception {
        Files.writeString(root.resolve("index.html"), "<h1>Slides</h1>");

        try (PresentationServer server = PresentationServer.start(root, 0)) {
            final HttpResponse<String> response = get(server, "/index.html");

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isEqualTo("<h1>Slides</h1>");
        }
    }

    @Test
    void answersNotFoundForMissingFiles() throws Exception {
        try (PresentationServer server = PresentationServer.start(root, 0)) {
            assertThat(get(server, "/missing.html").statusCode()).isEqualTo(404);
        }
    }

    @Test
    void rejectsMissingDirectory() {
        final Path missing = root.resolve("missing");

        assertThatThrownBy(() -> PresentationServer.start(missing, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Not a directory");
    }

    @Test
    void rejectsInvalidPort() {
        assertThatThrownBy(() -> PresentationServer.start(root, 70_000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid port");
    }

    private static HttpResponse<String> get(final PresentationServer server, final String path)
            throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            final HttpRequest request = HttpRequest.newBuilder(
                    URI.create("http://localhost:" + server.port() + path)).build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }
}
#end