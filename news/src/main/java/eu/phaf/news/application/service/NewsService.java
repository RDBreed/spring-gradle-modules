package eu.phaf.news.application.service;

import eu.phaf.news.application.gateway.NewsGateway;
import eu.phaf.news.domain.Error;
import eu.phaf.news.domain.Result;
import eu.phaf.news.domain.model.NewsArticle;
import eu.phaf.news.infrastructure.exception.InvalidCountryCodeException;

import java.util.List;

public class NewsService {
    private final NewsGateway newsApi;
    private final CountryValidator countryValidator;

    public NewsService(NewsGateway newsApi, CountryValidator countryValidator) {
        this.newsApi = newsApi;
        this.countryValidator = countryValidator;
    }

    public Result<List<NewsArticle>, Exception> getNewsForCountry(String country) {
        return countryValidator.isValid(country)
                .flatMap(isValid -> {
                    if (isValid) {
                        return newsApi.getNewsForCountry(country);
                    }
                    return Result.error(new Error<>(new InvalidCountryCodeException(country), "Invalid country code."));
                });
    }
}
