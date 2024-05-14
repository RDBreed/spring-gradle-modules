package eu.phaf.monadic;

import java.util.function.Function;

public record Result<T, U>(T value, Error<U> error) {
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

    public <V> Result<T, V> flatMapError(Function<Error<U>, Result<T, V>> f) {
        if (isError()) {
            return f.apply(error);
        }
        return Result.of(value);
    }

    public <V> Result<T, V> mapError(Function<Error<U>, Error<V>> f) {
        return flatMapError(f.andThen(Result::error));
    }

    public <V, Z> Result<V, Z> fold(Function<T, Result<V, Z>> left, Function<Error<U>, Result<V, Z>> right) {
        if (isError()) {
            return right.apply(error);
        }
        return left.apply(value);
    }
}
