package eu.phaf.stateman.retry;

public interface RetryJobHandler {
    void addRetryJob(RetryTask retryTask);
}
