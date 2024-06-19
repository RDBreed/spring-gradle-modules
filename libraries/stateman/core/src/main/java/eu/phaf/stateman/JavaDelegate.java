package eu.phaf.stateman;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

public abstract class JavaDelegate<T> {

    private final TaskManager taskManager;
    private final Class<T> theClass;

    protected JavaDelegate(TaskManager taskManager, Class<T> theClass, TaskManager.TaskManagerTask... taskManagerTasks) {
        this.taskManager = taskManager;
        this.theClass = theClass;
        for (TaskManager.TaskManagerTask taskManagerTask : taskManagerTasks) {
            if (taskManagerTask.retryTaskManagerTask() != null) {
                registerTask(taskManager, theClass, taskManagerTask, taskManagerTask.retryTaskManagerTask());
            } else {
                registerTask(taskManager, theClass, taskManagerTask);
            }
        }
    }

    public <R> R apply(Supplier<R> function, Map<String, Object> parameters) {
        var callable = StackWalker.getInstance(RETAIN_CLASS_REFERENCE)
                .walk(stackFrameStream -> stackFrameStream.skip(1).findFirst());
        return callable.map(stackFrame -> {
            try {
                taskManager.startTask(stackFrame.getMethodName(), theClass, parameters);
                R result = function.get();
                taskManager.endTask(stackFrame.getMethodName(), theClass, parameters);
                return result;
            } catch (Exception e) {
                taskManager.failTask(stackFrame.getMethodName(), theClass, parameters);
                throw e;
            }
        }).orElseGet(function);
    }

    private void registerTask(TaskManager taskManager, Class<T> theClass, TaskManager.TaskManagerTask taskManagerTask) {
        getMethod(taskManagerTask.methodName(),
                theClass)
                .ifPresent(method -> {
                    taskManager.registerTask(
                            taskManagerTask.methodName(),
                            theClass,
                            getParameterNames(method));
                });
    }

    private void registerTask(TaskManager taskManager, Class<T> theClass, TaskManager.TaskManagerTask taskManagerTask, TaskManager.TaskManagerTask.RetryTaskManagerTask retryTaskManagerTask) {
        getMethod(taskManagerTask.methodName(),
                theClass)
                .ifPresent(method -> {
                    taskManager.registerTask(
                            taskManagerTask.methodName(),
                            theClass,
                            getParameterNames(method),
                            retryTaskManagerTask.retryDuration(),
                            retryTaskManagerTask.maxAttempts(),
                            retryTaskManagerTask.retryMethod());
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
