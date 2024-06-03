package eu.phaf.stateman;

import java.util.ArrayList;
import java.util.List;

public interface TaskActionRepository {
    void save(TaskAction taskAction);

    class InMemoryTaskActionRepository implements TaskActionRepository {
        List<TaskAction> taskActions = new ArrayList<>();

        @Override
        public void save(TaskAction taskAction) {
            taskActions.add(taskAction);
        }
    }
}
