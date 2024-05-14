package eu.phaf.monadic;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class TrySupplierTest {
    @Test
    void get() {
        assertThat(new TrySupplier().get(() -> "TEST"))
                .usingRecursiveComparison()
                .isEqualTo(Result.of("TEST"));
    }

    @Test
    void getFail() {
        RuntimeException runtimeException = new RuntimeException();
        assertThat(new TrySupplier().get(() -> {
            throw runtimeException;
        }))
                .usingRecursiveComparison()
                .isEqualTo(Result.error(new Error<>(runtimeException, "Generic error")));
    }
}
