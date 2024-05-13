package eu.phaf.news.infrastructure.exception;

public class InvalidCountryCodeException extends RuntimeException {
    private final String countryCode;

    public InvalidCountryCodeException(String countryCode) {
        this.countryCode = countryCode;
    }
}
