package eu.phaf.openapi.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String errorBody) {
        super(errorBody);
    }
}
