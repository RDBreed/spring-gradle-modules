package eu.phaf.stateman;

public interface RetryJobHandler {
    void addRetryJob(RetryTask retryTask);
}
