package eu.phaf.stateman;

import java.time.OffsetDateTime;
import java.util.List;

public record TaskAction(Task task,
                         List<? extends ParameterClassAndValue<?>> parameterValues,
                         OffsetDateTime offsetDateTime,
                         TaskType taskType) {

    public enum TaskType {
        STARTED, ENDED, FAILED
    }

}
