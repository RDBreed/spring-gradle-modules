package eu.phaf.stateman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TaskRepository {

    void generateTable();

    void save(Task task);

    Optional<Task> getTask(String methodName, Class<?> theClass);

    class InMemoryTaskRepository implements TaskRepository {

        private Map<Class<?>, List<Task>> tasks = new HashMap<>();

        @Override
        public void generateTable() {
            // doing nothing
        }

        @Override
        public void save(Task task) {
            List<Task> taskList = tasks.getOrDefault(task.theClass(), new ArrayList<>());
            taskList.add(task);
            tasks.put(task.theClass(), taskList);
        }

        @Override
        public Optional<Task> getTask(String methodName, Class<?> theClass) {
            return tasks.entrySet().stream()
                    .filter(classTaskEntry -> classTaskEntry.getKey() == theClass)
                    .flatMap(classTaskEntry -> classTaskEntry.getValue().stream().filter(task -> task.methodName().equals(methodName)).findAny().stream())
                    .findAny();
        }
    }
}
