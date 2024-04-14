package eu.phaf.openapiconfiguration;

public class RetryableException extends RuntimeException {
    public RetryableException(String errorBody) {
        super(errorBody);
    }
}
