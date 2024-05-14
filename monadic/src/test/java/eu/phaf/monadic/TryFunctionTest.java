package eu.phaf.monadic;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class TryFunctionTest {
    @Test
    void apply() {
        assertThat(new TryFunction().apply(o -> o.toLowerCase(Locale.ROOT), "TEST"))
                .usingRecursiveComparison()
                .isEqualTo(Result.of("test"));
    }

    @Test
    void applyFail() {
        RuntimeException runtimeException = new RuntimeException();
        assertThat(new TryFunction().apply(o -> {
            throw runtimeException;
        }, "TEST"))
                .usingRecursiveComparison()
                .isEqualTo(Result.error(new Error<>(runtimeException, "Generic error")));
    }
}
