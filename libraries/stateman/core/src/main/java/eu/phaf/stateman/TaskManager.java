package eu.phaf.stateman;

import eu.phaf.stateman.retry.RetryTaskManager;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private final TaskRepository taskRepository;
    private final TaskActionRepository taskActionRepository;
    private final RetryTaskManager retryTaskManager;

    public TaskManager(TaskRepository taskRepository,
                       TaskActionRepository taskActionRepository,
                       RetryTaskManager retryTaskManager) {
        this.taskRepository = taskRepository;
        this.taskActionRepository = taskActionRepository;
        this.retryTaskManager = retryTaskManager;
    }

    public void registerTask(String methodName, Class<?> theClass, List<Class<?>> parameters) {
        taskRepository.save(new Task(theClass, methodName, parameters));
    }

    public void registerTask(String methodName, Class<?> theClass, List<Class<?>> parameters, Duration retryDuration, int maxAttempts, String retryMethod) {
        Task task = new Task(theClass, methodName, parameters);
        taskRepository.save(task);
        retryTaskManager.createRetryTask(task, retryDuration, maxAttempts, retryMethod);
    }

    public void startTask(String methodName, Class<?> theClass, Map<String, Object> parameters) {
        taskRepository.getTask(methodName, theClass)
                .ifPresent(task -> taskActionRepository.save(new TaskAction(task, parameters, OffsetDateTime.now(), TaskAction.TaskType.STARTED)));
    }

    public void endTask(String methodName, Class<?> theClass, Map<String, Object> parameters) {
        taskRepository.getTask(methodName, theClass)
                .ifPresent(task -> taskActionRepository.save(new TaskAction(task, parameters, OffsetDateTime.now(), TaskAction.TaskType.ENDED)));
    }

    public void failTask(String methodName, Class<?> theClass, Map<String, Object> parameters) {
        taskRepository.getTask(methodName, theClass)
                .ifPresent(task -> {
                    OffsetDateTime now = OffsetDateTime.now();
                    TaskAction taskAction = new TaskAction(task, parameters, now, TaskAction.TaskType.FAILED);
                    taskActionRepository.save(taskAction);
                    retryTaskManager.createRetryTaskActions(task, taskAction, now);
                });
    }

    public record TaskManagerTask(String methodName, RetryTaskManagerTask retryTaskManagerTask) {

        public TaskManagerTask(String methodName) {
            this(methodName, null);
        }

        public record RetryTaskManagerTask(Duration retryDuration, int maxAttempts, String retryMethod) {

        }
    }
}
