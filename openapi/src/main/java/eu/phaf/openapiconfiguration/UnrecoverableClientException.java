package eu.phaf.openapiconfiguration;

public class UnrecoverableClientException extends RuntimeException {
    public UnrecoverableClientException(String errorBody) {
        super(errorBody);
    }
}
