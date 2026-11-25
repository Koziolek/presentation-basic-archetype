package ${package};

import ${package}.cli.RootCommand;
import picocli.CommandLine;

/**
 * Application entry point.
 */
public final class App {

    private App() {
    }

    /**
     * Builds the command line with all registered commands. Exposed for tests.
     *
     * @return configured command line
     */
    public static CommandLine createCommandLine() {
        return new CommandLine(new RootCommand());
    }

    /**
     * Starts the application.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        System.exit(createCommandLine().execute(args));
    }
}
