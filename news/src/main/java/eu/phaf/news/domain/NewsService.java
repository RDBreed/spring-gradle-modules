package eu.phaf.news.domain;

import eu.phaf.news.port.client.NewsApi;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class NewsService {
    private final NewsApi newsApi;
    private final CountryValidator countryValidator;

    public NewsService(NewsApi newsApi, CountryValidator countryValidator) {
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
