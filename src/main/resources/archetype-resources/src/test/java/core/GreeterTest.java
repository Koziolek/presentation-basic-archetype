package ${package}.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class GreeterTest {

    private final Greeter greeter = new Greeter();

    @Test
    void greetsByTrimmedName() {
        assertThat(greeter.greet("  Ada  ").get()).isEqualTo("Hello, Ada!");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "   \n"})
    void rejectsBlankNames(final String name) {
        assertThat(greeter.greet(name).getLeft()).isEqualTo("Name must not be blank.");
    }

    @Test
    void rejectsTooLongNames() {
        final String name = "x".repeat(Greeter.MAX_NAME_LENGTH + 1);

        assertThat(greeter.greet(name).getLeft()).contains("longer than " + Greeter.MAX_NAME_LENGTH);
    }

    @Test
    void acceptsNameOfMaximumLength() {
        assertThat(greeter.greet("x".repeat(Greeter.MAX_NAME_LENGTH)).isRight()).isTrue();
    }

    @Test
    void greetAllSkipsInvalidNamesAndKeepsOrder() {
        assertThat(greeter.greetAll(List.of("Ada", " ", "Linus")))
                .containsExactly("Hello, Ada!", "Hello, Linus!");
    }
}
