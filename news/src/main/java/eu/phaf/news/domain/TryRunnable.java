package eu.phaf.news.domain;

public class TryRunnable {

    public Result<Void, Exception> run(Runnable runnable) {
        try {
            runnable.run();
            return Result.of(null);
        } catch (Exception exception) {
            return Result.error(new Error<>(exception, "Generic error"));
        }
    }
}
