package ${package}.cli;

import static org.assertj.core.api.Assertions.assertThat;

import ${package}.App;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import picocli.CommandLine.ExitCode;

class GreetCommandTest {

    private final StringWriter out = new StringWriter();
    private final StringWriter err = new StringWriter();
    private CommandLine commandLine;

    @BeforeEach
    void setUp() {
        commandLine = App.createCommandLine();
        commandLine.setOut(new PrintWriter(out));
        commandLine.setErr(new PrintWriter(err));
    }

    @Test
    void greetsWorldByDefault() {
        final int exitCode = commandLine.execute("greet");

        assertThat(exitCode).isEqualTo(ExitCode.OK);
        assertThat(out).hasToString("Hello, World!" + System.lineSeparator());
    }

    @Test
    void greetsEveryGivenName() {
        final int exitCode = commandLine.execute("greet", "Ada", "Linus");

        assertThat(exitCode).isEqualTo(ExitCode.OK);
        assertThat(out.toString()).contains("Hello, Ada!").contains("Hello, Linus!");
    }

    @Test
    void shoutsWhenUpperOptionIsGiven() {
        commandLine.execute("greet", "--upper", "Ada");

        assertThat(out.toString()).contains("HELLO, ADA!");
    }

    @Test
    void reportsInvalidNamesOnErrorStream() {
        final int exitCode = commandLine.execute("greet", " ");

        assertThat(exitCode).isEqualTo(ExitCode.USAGE);
        assertThat(err.toString()).contains("Name must not be blank.");
        assertThat(out.toString()).isEmpty();
    }

    @Test
    void printsUsageWithoutSubcommand() {
        final int exitCode = commandLine.execute();

        assertThat(exitCode).isEqualTo(ExitCode.OK);
        assertThat(out.toString()).contains("Usage:").contains("greet");
    }
}
