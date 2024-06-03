package eu.phaf.stateman;

import java.time.Duration;

public record RetryTask(Task task, Duration retryAfter, Integer maxAttempts, String retryMethod) {
}
