package eu.phaf.stateman;

import java.util.List;

public record Task(Class<?> theClass, String methodName, List<Class<?>> parameters) {
}
