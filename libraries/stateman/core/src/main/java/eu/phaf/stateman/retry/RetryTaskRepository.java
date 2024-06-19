package eu.phaf.stateman.retry;

import eu.phaf.stateman.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface RetryTaskRepository {
    void save(RetryTask retryTask);

    Optional<RetryTask> getByTask(Task task);

    class InMemoryRetryTaskRepository implements RetryTaskRepository {
        Map<Task, RetryTask> retryTasks = new HashMap<>();

        @Override
        public void save(RetryTask retryTask) {
            retryTasks.put(retryTask.task(), retryTask);
        }

        @Override
        public Optional<RetryTask> getByTask(Task task) {
            return Optional.ofNullable(retryTasks.getOrDefault(task, null));
        }
    }
}
