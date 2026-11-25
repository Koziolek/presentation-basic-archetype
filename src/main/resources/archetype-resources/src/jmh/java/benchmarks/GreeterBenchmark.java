package ${package}.benchmarks;

import ${package}.core.Greeter;
import io.vavr.control.Either;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class GreeterBenchmark {

    private final Greeter greeter = new Greeter();
    private final List<String> names = List.of("Ada", " ", "Linus", "Grace", "", "Barbara");

    @Benchmark
    public Either<String, String> greetValidName() {
        return greeter.greet("Ada");
    }

    @Benchmark
    public Either<String, String> greetBlankName() {
        return greeter.greet("   ");
    }

    @Benchmark
    public List<String> greetAll() {
        return greeter.greetAll(names);
    }
}
