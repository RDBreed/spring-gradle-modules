package eu.phaf.stateman;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

public class TaskManager {
    private final TaskRepository taskRepository;
    private final TaskActionRepository taskActionRepository;
    private final RetryTaskRepository retryTaskRepository;
    private final RetryTaskActionRepository retryTaskActionRepository;
    private final RetryJobHandler retryJobHandler;

    public TaskManager(TaskRepository taskRepository,
                       TaskActionRepository taskActionRepository,
                       RetryTaskRepository retryTaskRepository,
                       RetryTaskActionRepository retryTaskActionRepository,
                       RetryJobHandler retryJobHandler) {
        this.taskRepository = taskRepository;
        this.taskActionRepository = taskActionRepository;
        this.retryTaskRepository = retryTaskRepository;
        this.retryTaskActionRepository = retryTaskActionRepository;
        this.retryJobHandler = retryJobHandler;
    }

    public void registerTask(String methodName, Class<?> theClass) {
        getMethod(methodName, theClass)
                .ifPresent(method ->
                        taskRepository.save(new Task(theClass, methodName, getParameterNames(method))));
    }

    public void registerTask(String methodName, Class<?> theClass, Duration retryDuration, int maxAttempts, String retryMethod) {
        getMethod(methodName, theClass)
                .ifPresent(method ->
                        {
                            Task task = new Task(theClass, methodName, getParameterNames(method));
                            taskRepository.save(task);
                            RetryTask retryTask = new RetryTask(task, retryDuration, maxAttempts, retryMethod);
                            retryTaskRepository.save(retryTask);
                            retryJobHandler.addRetryJob(retryTask);
                        }
                );
    }

    public void startTask(Map<String, Object> parameters) {
        var callable = StackWalker.getInstance(RETAIN_CLASS_REFERENCE)
                .walk(stackFrameStream -> stackFrameStream.skip(1).findFirst())
                .orElseThrow(() -> new RuntimeException("Not found"));
        startTask(callable.getMethodName(), callable.getDeclaringClass(), parameters);
    }

    private void startTask(String methodName, Class<?> theClass, Map<String, Object> parameters) {
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
                    Optional<RetryTask> optionalRetryTask = retryTaskRepository.getByTask(task);
                    optionalRetryTask.ifPresent(retryTask -> {
                        for (int i = 1; i <= retryTask.maxAttempts(); i++) {
                            OffsetDateTime retryTime = now.plus(retryTask.retryAfter().multipliedBy(i));
                            System.out.println("retryTime " + retryTime);
                            retryTaskActionRepository.save(new RetryTaskAction(taskAction, retryTime));
                        }
                    });
                });
    }

    private Optional<Method> getMethod(String methodName, Class<?> theClass) {
        Method[] declaredMethods = theClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getName().equals(methodName) && Modifier.isPublic(declaredMethod.getModifiers())) {
                return Optional.of(declaredMethod);
            }
        }
        return Optional.empty();
    }


    private static List<Class<?>> getParameterNames(Method method) {
        Parameter[] parameters = method.getParameters();
        List<Class<?>> parameterNames = new ArrayList<>();

        for (Parameter parameter : parameters) {
            parameterNames.add(parameter.getType());
        }

        return parameterNames;
    }
}
