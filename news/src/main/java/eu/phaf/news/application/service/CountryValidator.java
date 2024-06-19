package eu.phaf.news.application.service;

import eu.phaf.stateman.retry.StoredRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.MissingResourceException;

public class CountryValidator {
    private final Logger LOG = LoggerFactory.getLogger(CountryValidator.class);

    public CountryValidator() {
    }

//    @StoredRetry(duration = "PT5s", maxAttempts = 3, retryMethod = "isValidRetry")
    public boolean isValid(String country) {
        LOG.info("validating {}", country);
        var locale = Locale.forLanguageTag("en-" + country);
        String iso3Country = locale.getISO3Country();
        return !iso3Country.isBlank();
    }

    public boolean isValidRetry(String country) {
        LOG.info("validating retry {}", country);
        try {
            var locale = Locale.forLanguageTag("en-" + country);
            String iso3Country = locale.getISO3Country();
            return !iso3Country.isBlank();
        } catch (MissingResourceException e) {
            LOG.info("invalid country {}", country);
            throw e;
        }
    }
}
