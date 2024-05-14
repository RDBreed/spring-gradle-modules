package eu.phaf.news.application.service;

import eu.phaf.news.domain.Result;
import eu.phaf.news.domain.TrySupplier;

import java.util.Locale;
import java.util.MissingResourceException;

public class CountryValidator {
    public Result<Boolean, Exception> isValid(String country) {
        return new TrySupplier().get(() -> {
            try {
                var locale = Locale.forLanguageTag("en-" + country);
                String iso3Country = locale.getISO3Country();
                return !iso3Country.isBlank();
            } catch (MissingResourceException e) {
                return false;
            }
        });
    }
}
