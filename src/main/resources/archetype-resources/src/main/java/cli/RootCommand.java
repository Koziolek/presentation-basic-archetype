package ${package}.cli;

#if ($includeWebServer.equalsIgnoreCase("Y"))
import ${package}.web.ServeCommand;
#end
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/**
 * Top-level command; prints usage when no sub-command is given.
 */
@Command(
        name = "${artifactId}",
        mixinStandardHelpOptions = true,
        version = "${artifactId} ${version}",
        description = "Console application generated from presentation-basic-archetype.",
        subcommands = {
            GreetCommand.class#if ($includeWebServer.equalsIgnoreCase("Y")),
            ServeCommand.class#end

        })
public final class RootCommand implements Runnable {

    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        spec.commandLine().usage(spec.commandLine().getOut());
    }
}
