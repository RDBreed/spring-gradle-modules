package eu.phaf.monadic;

public record Error<T>(T code, String message) {
}