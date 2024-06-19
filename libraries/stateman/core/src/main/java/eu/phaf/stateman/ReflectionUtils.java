package eu.phaf.stateman;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public final class ReflectionUtils {
    private ReflectionUtils() {
    }

    public static List<Class<?>> getParameterNames(Method method) {
        Parameter[] parameters = method.getParameters();
        List<Class<?>> parameterNames = new ArrayList<>();

        for (Parameter parameter : parameters) {
            parameterNames.add(parameter.getType());
        }

        return parameterNames;
    }
}
