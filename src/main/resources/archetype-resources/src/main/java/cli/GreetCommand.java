package ${package}.cli;

import ${package}.core.Greeter;
import io.vavr.control.Either;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

/**
 * Prints a greeting for every given name ("World" when none is given).
 */
@Command(
        name = "greet",
        mixinStandardHelpOptions = true,
        description = "Prints a greeting for each given name.")
public final class GreetCommand implements Callable<Integer> {

    private static final String DEFAULT_NAME = "World";

    private final Greeter greeter = new Greeter();

    @Spec
    private CommandSpec spec;

    @Parameters(arity = "0..*", paramLabel = "NAME", description = "Names to greet.")
    private List<String> names = List.of();

    @Option(names = {"-u", "--upper"}, description = "Print greetings in upper case.")
    private boolean upper;

    @Override
    public Integer call() {
        final PrintWriter out = spec.commandLine().getOut();
        final PrintWriter err = spec.commandLine().getErr();
        final List<String> targets = names.isEmpty() ? List.of(DEFAULT_NAME) : names;

        boolean failed = false;
        for (final String name : targets) {
            final Either<String, String> result = greeter.greet(name);
            if (result.isRight()) {
                out.println(upper ? result.get().toUpperCase(Locale.ROOT) : result.get());
            } else {
                err.println(result.getLeft());
                failed = true;
            }
        }
        return failed ? ExitCode.USAGE : ExitCode.OK;
    }
}
