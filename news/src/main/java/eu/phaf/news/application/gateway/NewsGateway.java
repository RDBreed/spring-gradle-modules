package eu.phaf.news.application.gateway;

import eu.phaf.news.domain.model.NewsArticle;
import reactor.core.publisher.Flux;

public interface NewsGateway {
    Flux<NewsArticle> getNewsForCountry(String country);
}
