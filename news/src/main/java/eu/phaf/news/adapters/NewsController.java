package eu.phaf.news.adapters;

import eu.phaf.openapi.newsapiv2_0_0.model.NewsArticleResponse;
import eu.phaf.openapi.newsapiv2_0_0.server.api.NewsApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class NewsController implements NewsApi {
    @Override
    public Mono<ResponseEntity<Flux<NewsArticleResponse>>> getNewsByCountry(String country, ServerWebExchange exchange) {
        return null;
    }
}
