package eu.phaf.stateman.retry;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface RetryJob {

    class SimpleRetryJob implements RetryJob {

        private final ScheduledFuture<?> job;
        private final ScheduledExecutorService scheduledThreadPoolExecutor;

        public SimpleRetryJob(RetryTaskActionRepository retryTaskActionRepository,
                              RetryTask retryTask,
                              Consumer<RetryTaskAction> retryTaskActionConsumer) {
            scheduledThreadPoolExecutor = Executors.newScheduledThreadPool(1);
            try {
                job = getScheduledJob(retryTaskActionRepository, retryTask, scheduledThreadPoolExecutor, retryTaskActionConsumer);
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        }

        private ScheduledFuture<?> getScheduledJob(RetryTaskActionRepository retryTaskActionRepository,
                                                   RetryTask retryTask,
                                                   ScheduledExecutorService scheduledThreadPoolExecutor,
                                                   Consumer<RetryTaskAction> retryTaskActionConsumer) {
            var task = retryTask.task();
            ScheduledFuture<?> scheduledFuture = scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
                Optional<RetryTaskAction> firstRetryTaskAction = retryTaskActionRepository.getAndRemoveFirstRetryTaskAction(task, retryTask.retryMethod(), OffsetDateTime.now());
                firstRetryTaskAction.ifPresent(retryTaskActionConsumer);
            }, 0, 100L, TimeUnit.MILLISECONDS);
            return scheduledFuture;
        }
    }
}
