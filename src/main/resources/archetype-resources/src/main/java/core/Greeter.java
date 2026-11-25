package ${package}.core;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;
import io.vavr.control.Either;
import io.vavr.control.Option;
import java.util.List;

/**
 * Business logic of the sample application. It knows nothing about the console or the web.
 */
public final class Greeter {

    /** Maximum accepted length of a name. */
    public static final int MAX_NAME_LENGTH = 50;

    /**
     * Builds a greeting.
     *
     * @param name name to greet, may be {@code null}
     * @return the greeting, or a validation message when the name is not acceptable
     */
    public Either<String, String> greet(final String name) {
        final String trimmed = Option.of(name)
                .map(CharMatcher.whitespace()::trimFrom)
                .getOrElse("");
        if (trimmed.isEmpty()) {
            return Either.left("Name must not be blank.");
        }
        if (trimmed.length() > MAX_NAME_LENGTH) {
            return Either.left("Name must not be longer than " + MAX_NAME_LENGTH + " characters.");
        }
        return Either.right("Hello, " + trimmed + "!");
    }

    /**
     * Builds greetings for all acceptable names and silently skips the others.
     *
     * @param names names to greet
     * @return greetings in the order of the input
     */
    public ImmutableList<String> greetAll(final List<String> names) {
        return names.stream()
                .map(this::greet)
                .filter(Either::isRight)
                .map(Either::get)
                .collect(ImmutableList.toImmutableList());
    }
}
