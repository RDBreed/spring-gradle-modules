package eu.phaf.openapiconfiguration;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String errorBody) {
        super(errorBody);
    }
}
