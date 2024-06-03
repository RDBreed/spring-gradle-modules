package eu.phaf.stateman;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface RetryTaskActionRepository {
    void save(RetryTaskAction retryTaskAction);

    Optional<RetryTaskAction> getFirstRetryTaskAction(Task task);

    int count(Class<?> theClass);

    class InMemoryRetryTaskActionRepository implements RetryTaskActionRepository {
        Map<Task, Map<OffsetDateTime, RetryTaskAction>> retryTasks = new HashMap<>();

        @Override
        public void save(RetryTaskAction retryTaskAction) {
            retryTasks.putIfAbsent(retryTaskAction.taskAction().task(), new HashMap<>());
            retryTasks.get(retryTaskAction.taskAction().task()).put(retryTaskAction.offsetDateTime(), retryTaskAction);
        }

        @Override
        public Optional<RetryTaskAction> getFirstRetryTaskAction(Task task) {
            Map<OffsetDateTime, RetryTaskAction> actions = retryTasks.getOrDefault(task, Collections.emptyMap());
            Optional<RetryTaskAction> first = actions
                    .entrySet()
                    .stream()
                    .sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey()))
                    .filter(offsetDateTimeRetryTaskActionEntry -> OffsetDateTime.now().isBefore(offsetDateTimeRetryTaskActionEntry.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst();
            first.ifPresent(retryTaskAction -> actions.remove(retryTaskAction.offsetDateTime()));
            return first;
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
