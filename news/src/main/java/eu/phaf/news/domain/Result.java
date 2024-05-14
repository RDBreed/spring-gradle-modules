package eu.phaf.news.domain;

import java.util.function.Function;

public class Result<T, U> {
    private final T value;
    private final Error<U> error;

    Result(T value, Error<U> error) {
        this.value = value;
        this.error = error;
    }

    public static <T, U> Result<T, U> of(T value) {
        return new Result<>(value, null);
    }

    public static <T, U> Result<T, U> error(Error<U> error) {
        return new Result<>(null, error);
    }

    public <V> Result<V, U> flatMap(Function<T, Result<V, U>> f) {
        if (isError()) {
            return Result.error(error);
        }
        return f.apply(value);
    }

    public <V> Result<V, U> map(Function<T, V> f) {
        return flatMap(f.andThen(Result::of));
    }

    public boolean isError() {
        return this.error != null;
    }

    public T getValue() {
        return this.value;
    }

    public Error<U> getError() {
        return this.error;
    }

    public T orElse(Function<Error<U>, T> f) {
        if (isError()) {
            return f.apply(error);
        }
        return value;
    }

    public <E extends RuntimeException> T orThrow(Function<Error<U>, E> f) throws E {
        if (isError()) {
            throw f.apply(error);
        }
        return value;
    }
}