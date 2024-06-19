package eu.phaf.news.application.service;

import eu.phaf.stateman.JavaDelegate;
import eu.phaf.stateman.TaskManager;
import eu.phaf.stateman.TaskManager.TaskManagerTask.RetryTaskManagerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

import static eu.phaf.stateman.TaskManager.TaskManagerTask;

public class CountryValidator extends JavaDelegate<CountryValidator> {
    private final Logger LOG = LoggerFactory.getLogger(CountryValidator.class);

    public CountryValidator(TaskManager taskManager) {
        super(taskManager, CountryValidator.class,
                new TaskManagerTask("isValid", new RetryTaskManagerTask(Duration.ofSeconds(5), 3, "isValidRetry")),
                new TaskManagerTask("isValidRetry"));
    }

    public boolean isValid(String country) {
        return apply(() -> isValidDelegate(country), Map.of("country", country));
    }

    public boolean isValidDelegate(String country) {
        LOG.info("validating {}", country);
        var locale = Locale.forLanguageTag("en-" + country);
        String iso3Country = locale.getISO3Country();
        return !iso3Country.isBlank();
    }

    public boolean isValidRetry(String country) {
        return apply(() -> isValidRetryDelegate(country), Map.of("country", country));
    }

    public boolean isValidRetryDelegate(String country) {
        LOG.info("validating retry {}", country);
        try {
            var locale = Locale.forLanguageTag("en-" + country);
            String iso3Country = locale.getISO3Country();
            return !iso3Country.isBlank();
        } catch (MissingResourceException e) {
            LOG.info("invalid country {}", country);
            throw e;
//            return false;
        }
    }
}
