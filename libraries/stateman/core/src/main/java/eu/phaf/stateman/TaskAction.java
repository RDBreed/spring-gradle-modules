package eu.phaf.stateman;

import java.time.OffsetDateTime;
import java.util.Map;

public record TaskAction(Task task, Map<String, Object> parameterValues, OffsetDateTime offsetDateTime, TaskType taskType) {

    public enum TaskType {
        STARTED, ENDED, FAILED
    }
}
