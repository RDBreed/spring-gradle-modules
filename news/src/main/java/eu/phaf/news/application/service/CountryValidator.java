package eu.phaf.news.application.service;

import eu.phaf.stateman.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;

public class CountryValidator {
    private final TaskManager taskManager;
    private final Logger LOG = LoggerFactory.getLogger(CountryValidator.class);

    public CountryValidator(TaskManager taskManager) {
        this.taskManager = taskManager;
        taskManager.registerTask("isValid", this.getClass(), Duration.ofSeconds(5), 3, "isValidRetry");
        taskManager.registerTask("isValidRetry", this.getClass());
    }

    public boolean isValid(String country) {
        taskManager.startTask(Map.of("country", country));
        LOG.info("validating {}", country);
        try {
//        try {
            var locale = Locale.forLanguageTag("en-" + country);
            String iso3Country = locale.getISO3Country();
            taskManager.endTask("isValid", this.getClass(), Map.of("country", country));
            return !iso3Country.isBlank();
//        }
//        catch (MissingResourceException e) {
//            return false;
//        }
        } catch (Exception e) {
            taskManager.failTask("isValid", this.getClass(), Map.of("country", country));
            throw e;
        }
    }

    public boolean isValidRetry(String country) {
        taskManager.startTask(Map.of("country", country));
        LOG.info("validating {}", country);
        try {
//        try {
            var locale = Locale.forLanguageTag("en-" + country);
            String iso3Country = locale.getISO3Country();
            taskManager.endTask("isValidRetry", this.getClass(), Map.of("country", country));
            return !iso3Country.isBlank();
//        }
//        catch (MissingResourceException e) {
//            return false;
//        }
        } catch (Exception e) {
            taskManager.failTask("isValidRetry", this.getClass(), Map.of("country", country));
            throw e;
        }
    }
}
