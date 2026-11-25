#if ($includeWebServer.equalsIgnoreCase("Y"))##
package ${package}.web;

import com.google.common.base.Preconditions;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import com.sun.net.httpserver.SimpleFileServer.OutputLevel;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Minimal static web server (JDK {@link SimpleFileServer}) that exposes the files of a directory,
 * by default the {@code presentation} directory. It listens on the loopback interface only.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PresentationServer implements AutoCloseable {

    private static final int MAX_PORT = 65_535;

    private final HttpServer server;

    /**
     * Starts the server.
     *
     * @param root directory with the files to serve
     * @param port port to listen on, {@code 0} picks a free one
     * @return running server
     * @throws IOException when the port cannot be bound
     */
    public static PresentationServer start(final Path root, final int port) throws IOException {
        Preconditions.checkArgument(Files.isDirectory(root), "Not a directory: %s", root);
        Preconditions.checkArgument(port >= 0 && port <= MAX_PORT, "Invalid port: %s", port);
        final HttpServer server = SimpleFileServer.createFileServer(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), port),
                root.toAbsolutePath(),
                OutputLevel.INFO);
        server.start();
        return new PresentationServer(server);
    }

    /**
     * Returns the port the server really listens on.
     *
     * @return bound port
     */
    public int port() {
        return server.getAddress().getPort();
    }

    @Override
    public void close() {
        server.stop(0);
    }
}
#end