package eu.phaf.monadic;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class TryConsumerTest {
    @Test
    void accept() {
        assertThat(new TryConsumer().accept(o -> o.toLowerCase(Locale.ROOT), ""))
                .usingRecursiveComparison()
                .isEqualTo(Result.of(null));
    }

    @Test
    void acceptFail() {
        RuntimeException runtimeException = new RuntimeException();
        assertThat(new TryConsumer().accept(o -> {
            throw runtimeException;
        }, ""))
                .usingRecursiveComparison()
                .isEqualTo(Result.error(new Error<>(runtimeException, "Generic error")));
    }
}
