package eu.phaf.stateman.retry;

import eu.phaf.stateman.Task;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public record RetryTaskAction(Task task,
                              String retryMethod,
                              Map<String, Object> parameterValues,
                              OffsetDateTime originalEventTime,
                              List<OffsetDateTime> offsetDateTimes) {
    public OffsetDateTime getFirstOffsetDateTime() {
        return offsetDateTimes.getFirst();
    }

    public void removeFirstOffsetDateTime(){
        offsetDateTimes().removeFirst();
    }
}
