package eu.phaf.news.domain;

import java.util.function.Function;

public class TryFunction {

    public <T, R> Result<R, Exception> apply(Function<T, R> function, T parameter) {
        try {
            return Result.of(function.apply(parameter));
        } catch (Exception exception) {
            return Result.error(new Error<>(exception, "Generic error"));
        }
    }
}
