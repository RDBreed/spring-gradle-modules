package eu.phaf.news.application.service;

import eu.phaf.news.application.gateway.NewsGateway;
import eu.phaf.news.domain.model.NewsArticle;
import eu.phaf.news.infrastructure.exception.InvalidCountryCodeException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class NewsService {
    private final NewsGateway newsApi;
    private final CountryValidator countryValidator;

    public NewsService(NewsGateway newsApi, CountryValidator countryValidator) {
        this.newsApi = newsApi;
        this.countryValidator = countryValidator;
    }

    public Flux<NewsArticle> getNewsForCountry(String country) {
        return Mono.fromCallable(() -> countryValidator.isValid(country))
                .flatMapMany(isValid -> isValid ?
                        newsApi.getNewsForCountry(country) :
                        Flux.error(new InvalidCountryCodeException(country)));
    }
}
