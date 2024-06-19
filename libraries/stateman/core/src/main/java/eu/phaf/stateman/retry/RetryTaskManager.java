package eu.phaf.stateman.retry;

import eu.phaf.stateman.Task;
import eu.phaf.stateman.TaskAction;

import java.time.Duration;
import java.time.OffsetDateTime;

public interface RetryTaskManager {
    void createRetryTask(Task task, Duration retryDuration, Integer maxAttempts, String retryMethod);

    void createRetryTaskActions(Task task, TaskAction taskAction, OffsetDateTime now);

}
