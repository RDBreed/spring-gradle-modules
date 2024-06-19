package eu.phaf.stateman.retry;

import eu.phaf.stateman.Task;

import java.time.Duration;

public record RetryTask(Task task, Duration retryAfter, Integer maxAttempts, String retryMethod) {
}
