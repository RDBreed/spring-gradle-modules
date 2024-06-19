package eu.phaf.stateman.retry;

import eu.phaf.stateman.ParameterClassAndValue;
import eu.phaf.stateman.TaskManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
public class StoredRetryAspect {
    private final TaskManager taskManager;

    public StoredRetryAspect(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Around("@annotation(storedRetry)")
    public Object webFluxErrorHandling(ProceedingJoinPoint proceedingJoinPoint,
                                     StoredRetry storedRetry) throws Throwable {
        Object returnValue = proceedingJoinPoint.proceed();
        if (returnValue instanceof Mono<?> mono) {
            return mono.doOnError(throwable -> {
                var parameterClassAndValues = Arrays.stream(proceedingJoinPoint.getArgs())
                        .map(o -> getParameterClassAndValue(o.getClass(), o))
                        .toList();
                taskManager.failTask(proceedingJoinPoint.getSignature().getName(), proceedingJoinPoint.getSignature().getDeclaringType(), parameterClassAndValues);
            });
        }
        if (returnValue instanceof Flux<?> flux) {
            return flux.doOnError(throwable -> {
                var parameterClassAndValues = Arrays.stream(proceedingJoinPoint.getArgs())
                        .map(o -> getParameterClassAndValue(o.getClass(), o))
                        .toList();
                taskManager.failTask(proceedingJoinPoint.getSignature().getName(), proceedingJoinPoint.getSignature().getDeclaringType(), parameterClassAndValues);
            });
        }
        return returnValue;
    }

    @AfterThrowing("@annotation(storedRetry)")
    public void afterThrowing(JoinPoint joinPoint,
                                StoredRetry storedRetry) throws Throwable {
        var parameterClassAndValues = Arrays.stream(joinPoint.getArgs())
                .map(o -> getParameterClassAndValue(o.getClass(), o))
                .toList();
        taskManager.failTask(joinPoint.getSignature().getName(), joinPoint.getSignature().getDeclaringType(), parameterClassAndValues);
    }

    private static <T> ParameterClassAndValue<T> getParameterClassAndValue(Class<T> tClass, Object o) {
        return new ParameterClassAndValue<>(tClass, (T) o);
    }
}
