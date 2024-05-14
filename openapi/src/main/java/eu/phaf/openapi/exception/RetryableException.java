package eu.phaf.openapi.exception;

public class RetryableException extends RuntimeException {
    public RetryableException(String errorBody) {
        super(errorBody);
    }
}
