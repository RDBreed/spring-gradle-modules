package eu.phaf.monadic;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResultTest {

    @Test
    void of() {
        assertThat(Result.of("test"))
                .usingRecursiveComparison()
                .isEqualTo(Result.of("test"));
    }

    @Test
    void error() {
        assertThat(Result.error(new Error<>(123, "error")))
                .usingRecursiveComparison()
                .isEqualTo(Result.error(new Error<>(123, "error")));
    }

    @Test
    void flatMap() {
        assertThat(Result.of("test")
                .flatMap(s -> Result.of(1)))
                .usingRecursiveComparison()
                .isEqualTo(Result.of(1));
        assertThat(Result.error(new Error<>(123, "error"))
                .flatMap(s -> Result.of(1)))
                .usingRecursiveComparison()
                .isEqualTo(Result.error(new Error<>(123, "error")));
    }

    @Test
    void map() {
        assertThat(Result.of("test")
                .map(s -> 1))
                .usingRecursiveComparison()
                .isEqualTo(Result.of(1));
    }

    @Test
    void isError() {
        assertThat(Result.error(new Error<>(123, "error")))
                .extracting(Result::isError)
                .isEqualTo(true);
        assertThat(Result.of("test"))
                .extracting(Result::isError)
                .isEqualTo(false);
    }

    @Test
    void getValue() {
        assertThat(Result.of("test"))
                .extracting(Result::getValue)
                .isEqualTo("test");
    }

    @Test
    void getError() {
        assertThat(Result.error(new Error<>(123, "error")))
                .extracting(Result::getError)
                .usingRecursiveComparison()
                .isEqualTo(new Error<>(123, "error"));
    }

    @Test
    void orElse() {
        assertThat(Result.error(new Error<>(123, "error"))
                .orElse(integerError -> "value"))
                .isEqualTo("value");
        assertThat(Result.of("123")
                .orElse(integerError -> "value"))
                .isEqualTo("123");
    }

    @Test
    void orThrow() {
        assertThatThrownBy(() -> Result.error(new Error<>(123, "error"))
                .orThrow(integerError -> new RuntimeException("123")))
                .isExactlyInstanceOf(RuntimeException.class);
        assertThat(Result.of("123")
                .orThrow(integerError -> new RuntimeException("123")))
                .isEqualTo("123");
    }

    @Test
    void flatMapError() {
        assertThat(Result.error(new Error<>(123, "error"))
                .flatMapError(integerError -> Result.error(new Error<>("123", "error2"))))
                .usingRecursiveComparison()
                .isEqualTo(Result.error(new Error<>("123", "error2")));
        assertThat(Result.of("123")
                .flatMapError(integerError -> Result.error(new Error<>("123", "error2"))))
                .usingRecursiveComparison()
                .isEqualTo(Result.of("123"));
    }

    @Test
    void mapError() {
        assertThat(Result.error(new Error<>(123, "error"))
                .mapError(integerError -> new Error<>("123", "error2")))
                .usingRecursiveComparison()
                .isEqualTo(Result.error(new Error<>("123", "error2")));
        assertThat(Result.of("123")
                .mapError(integerError -> new Error<>("123", "error2")))
                .usingRecursiveComparison()
                .isEqualTo(Result.of("123"));
    }

    @Test
    void fold() {
        Function<Error<Object>, Result<String, Integer>> errorResultFunction = e -> Result.error(new Error(4, e.message()));
        assertThat(Result.of(new Request("123"))
                .fold(ResultTest::getResult, errorResultFunction))
                .usingRecursiveComparison()
                .isEqualTo(Result.of("entity"));
        assertThat(Result.of(new Request("345"))
                .fold(ResultTest::getResult, errorResultFunction))
                .usingRecursiveComparison()
                .isEqualTo(Result.error(new Error<>(2, "345 msg")));
        assertThat(Result.of(new Request("gen"))
                .fold(ResultTest::getResult, errorResultFunction))
                .usingRecursiveComparison()
                .isEqualTo(Result.error(new Error<>(3, "generic msg")));
        Result<Request, String> generic = Result.error(new Error<>("123", "generic"));
        assertThat(generic
                .fold(ResultTest::getResult, e -> Result.error(new Error(4, e.message()))))
                .usingRecursiveComparison()
                .isEqualTo(Result.error(new Error<>(4, "generic")));
    }

    private static Result<String, Integer> getResult(Request r) {
        return switch (r.status()) {
            case "123" -> r.getEntity()
                    .mapError(e -> new Error<>(1, e.message()));
            case "345" -> r.getEntity()
                    .mapError(e -> new Error<>(2, e.message()));
            default -> r.getEntity()
                    .mapError(e -> new Error<>(3, e.message()));
        };
    }

    public record Request(String status) {
        public Result<String, Exception> getEntity() {
            if (status.equals("123")) {
                return Result.of("entity");
            } else if (status.equals("345")) {
                return Result.error(new Error<>(new RuntimeException("345 error"), "345 msg"));
            } else {
                return Result.error(new Error<>(new RuntimeException("generic error"), "generic msg"));
            }
        }
    }
}