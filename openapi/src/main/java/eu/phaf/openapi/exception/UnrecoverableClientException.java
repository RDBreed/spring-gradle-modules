package eu.phaf.openapi.exception;

public class UnrecoverableClientException extends RuntimeException {
    public UnrecoverableClientException(String errorBody) {
        super(errorBody);
    }
}
