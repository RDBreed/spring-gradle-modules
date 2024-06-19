package eu.phaf.stateman.retry;

import eu.phaf.stateman.Task;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public interface RetryTaskActionRepository {
    void save(RetryTaskAction retryTaskAction);

    void remove(RetryTaskAction retryTaskAction);

    Optional<RetryTaskAction> getAndRemoveFirstRetryTaskAction(Task task, String retryMethod, OffsetDateTime now);


    Optional<RetryTaskAction> getFirstRetryTaskAction(Task task, String retryMethod, OffsetDateTime now);

    int count(Class<?> theClass);

    class InMemoryRetryTaskActionRepository implements RetryTaskActionRepository {
        Map<Task, List<RetryTaskAction>> retryTasks = new ConcurrentHashMap<>();

        @Override
        public void save(RetryTaskAction retryTaskAction) {
            retryTasks.putIfAbsent(retryTaskAction.task(), new ArrayList<>());
            retryTasks.get(retryTaskAction.task()).add(retryTaskAction);
        }

        @Override
        public void remove(RetryTaskAction retryTaskAction) {
            retryTasks.getOrDefault(retryTaskAction.task(), Collections.emptyList()).remove(retryTaskAction);
        }

        @Override
        public Optional<RetryTaskAction> getAndRemoveFirstRetryTaskAction(Task task, String retryMethod, OffsetDateTime now) {
            Optional<RetryTaskAction> firstRetryTaskAction = getFirstRetryTaskAction(task, retryMethod, now);
            firstRetryTaskAction.ifPresent(this::remove);
            return firstRetryTaskAction;
        }

        @Override
        public Optional<RetryTaskAction> getFirstRetryTaskAction(Task task, String retryMethod, OffsetDateTime now) {
            List<RetryTaskAction> actions = retryTasks.getOrDefault(task, Collections.emptyList());
            return actions
                    .stream()
                    .filter(retryTaskAction -> now.isAfter(retryTaskAction.getFirstOffsetDateTime()))
                    .findFirst();
        }

        @Override
        public int count(Class<?> theClass) {
            return retryTasks
                    .entrySet()
                    .stream()
                    .filter(taskMapEntry -> taskMapEntry.getKey().theClass().equals(theClass))
                    .findAny()
                    .map(taskMapEntry -> taskMapEntry.getValue().size())
                    .orElse(0);
        }
    }
}
