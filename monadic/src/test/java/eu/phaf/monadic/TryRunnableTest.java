package eu.phaf.monadic;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class TryRunnableTest {
    @Test
    void run() {
        assertThat(new TryRunnable().run(() -> "TEST".toLowerCase(Locale.ROOT)))
                .usingRecursiveComparison()
                .isEqualTo(Result.of(null));
    }

    @Test
    void runFail() {
        RuntimeException runtimeException = new RuntimeException();
        assertThat(new TryRunnable().run(() -> {
            throw runtimeException;
        }))
                .usingRecursiveComparison()
                .isEqualTo(Result.error(new Error<>(runtimeException, "Generic error")));
    }
}
