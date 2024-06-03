package eu.phaf.stateman;

import java.time.OffsetDateTime;

public record RetryTaskAction(TaskAction taskAction, OffsetDateTime offsetDateTime) {
}
