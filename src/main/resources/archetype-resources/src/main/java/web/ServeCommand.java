#if ($includeWebServer.equalsIgnoreCase("Y"))##
package ${package}.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

/**
 * Serves the {@code presentation} directory over HTTP until the process is stopped.
 */
@Command(
        name = "serve",
        mixinStandardHelpOptions = true,
        description = "Serves files from the presentation directory over HTTP.")
public final class ServeCommand implements Callable<Integer> {

    private final Consumer<PresentationServer> awaitTermination;

    @Spec
    private CommandSpec spec;

    @Option(names = {"-p", "--port"}, defaultValue = "8080", description = "Port to listen on.")
    private int port;

    @Option(names = {"-d", "--dir"}, defaultValue = "presentation", description = "Directory to serve.")
    private Path directory;

    /**
     * Creates the command that blocks until the JVM is stopped.
     */
    public ServeCommand() {
        this(ServeCommand::blockUntilStopped);
    }

    /**
     * Creates the command with a custom "wait" strategy. Used by tests.
     *
     * @param awaitTermination called with the running server; returning makes the command finish
     */
    ServeCommand(final Consumer<PresentationServer> awaitTermination) {
        this.awaitTermination = awaitTermination;
    }

    @Override
    public Integer call() throws IOException {
        final PrintWriter out = spec.commandLine().getOut();
        try (PresentationServer server = PresentationServer.start(directory, port)) {
            out.printf("Serving %s at http://localhost:%d/ (Ctrl+C to stop)%n", directory, server.port());
            out.flush();
            awaitTermination.accept(server);
        }
        return ExitCode.OK;
    }

    private static void blockUntilStopped(final PresentationServer server) {
        Runtime.getRuntime().addShutdownHook(new Thread(server::close));
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
#end