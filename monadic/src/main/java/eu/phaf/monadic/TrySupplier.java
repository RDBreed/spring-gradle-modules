package eu.phaf.monadic;

import java.util.function.Supplier;

public class TrySupplier {

    public <R> Result<R, Exception> get(Supplier<R> function) {
        try {
            return Result.of(function.get());
        } catch (Exception exception) {
            return Result.error(new Error<>(exception, "Generic error"));
        }
    }
}
