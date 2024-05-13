package eu.phaf.news.application.service;

import java.util.Locale;
import java.util.MissingResourceException;

public class CountryValidator {
    public boolean isValid(String country) {
        try {
            var locale = Locale.forLanguageTag("en-" + country);
            String iso3Country = locale.getISO3Country();
            return !iso3Country.isBlank();
        } catch (MissingResourceException e) {
            return false;
        }
    }
}
