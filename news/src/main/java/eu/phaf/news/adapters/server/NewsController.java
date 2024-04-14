package eu.phaf.news.adapters.server;

import eu.phaf.news.domain.NewsService;
import eu.phaf.openapi.newsapiv2_0_0.model.NewsArticleResponse;
import eu.phaf.openapi.newsapiv2_0_0.server.api.NewsApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class NewsController implements NewsApi {
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @Override
    public Mono<ResponseEntity<Flux<NewsArticleResponse>>> getNewsByCountry(String country, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(newsService.getNewsForCountry(country)
                .map(newsArticle -> new NewsArticleResponse()
                        .author(newsArticle.author())
                        .description(newsArticle.description())
                        .title(newsArticle.title())
                        .url(newsArticle.url())
                        .sourceName(newsArticle.source())
                        .image(newsArticle.imageInBytes())
                )));
    }
}
