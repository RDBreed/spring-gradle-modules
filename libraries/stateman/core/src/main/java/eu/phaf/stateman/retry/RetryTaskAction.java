package eu.phaf.stateman.retry;

import eu.phaf.stateman.ParameterClassAndValue;
import eu.phaf.stateman.Task;

import java.time.OffsetDateTime;
import java.util.List;

public record RetryTaskAction(Task task,
                              String retryMethod,
                              List<? extends ParameterClassAndValue<?>> parameterValues,
                              OffsetDateTime originalEventTime,
                              List<OffsetDateTime> offsetDateTimes) {
    public OffsetDateTime getFirstOffsetDateTime() {
        return offsetDateTimes.getFirst();
    }

    public void removeFirstOffsetDateTime(){
        offsetDateTimes().removeFirst();
    }
}
