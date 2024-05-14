package eu.phaf.news.domain;

public class Error<T> {
    private T code;
    private String message;

    public Error(T code, String message) {
        this.code = code;
        this.message = message;
    }

    public T getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}