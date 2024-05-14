package eu.phaf.monadic;

import java.util.function.Consumer;

public class TryConsumer {

    public <R> Result<Void, Exception> accept(Consumer<R> consumer, R parameter) {
        try {
            consumer.accept(parameter);
            return Result.of(null);
        } catch (Exception exception) {
            return Result.error(new Error<>(exception, "Generic error"));
        }
    }
}
