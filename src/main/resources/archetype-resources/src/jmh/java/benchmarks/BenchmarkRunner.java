package ${package}.benchmarks;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Runs the JMH benchmarks. Started by {@code mvn verify -Pbenchmarks}; the benchmark selection
 * comes from the {@code jmh.include} system property (regular expression).
 */
public final class BenchmarkRunner {

    private BenchmarkRunner() {
    }

    /**
     * Runs the selected benchmarks and writes the results to {@code target/jmh-result.json}.
     *
     * @param args ignored
     * @throws RunnerException when JMH fails
     */
    public static void main(final String[] args) throws RunnerException {
        final Options options = new OptionsBuilder()
                .include(System.getProperty("jmh.include", ".*"))
                .resultFormat(ResultFormatType.JSON)
                .result("target/jmh-result.json")
                .build();
        new Runner(options).run();
    }
}
