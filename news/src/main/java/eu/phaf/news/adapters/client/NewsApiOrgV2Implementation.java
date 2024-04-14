package eu.phaf.news.adapters.client;

import eu.phaf.news.domain.NewsArticle;
import eu.phaf.news.port.client.NewsApi;
import eu.phaf.openapi.newsapiv2_0_0.client.api.TopHeadlinesApi;
import eu.phaf.openapi.newsapiv2_0_0.client.model.NewsResponse;
import eu.phaf.openapi.newsapiv2_0_0.client.model.NewsSource;
import eu.phaf.openapiconfiguration.OpenApiClientConfiguration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class NewsApiOrgV2Implementation extends OpenApiClientConfiguration implements NewsApi {
    private final TopHeadlinesApi topHeadlinesApi;
    private final NewsApiOrgV2Configuration newsApiOrgV2Configuration;

    public NewsApiOrgV2Implementation(NewsApiOrgV2Configuration newsApiOrgV2Configuration) {
        this.newsApiOrgV2Configuration = newsApiOrgV2Configuration;
        this.topHeadlinesApi = new TopHeadlinesApi(apiClient(OpenApiProperties
                .builder()
                .basePath(newsApiOrgV2Configuration.basePath())
                .build()));
    }

    @Override
    public Flux<NewsArticle> getNewsForCountry(String country) {
        return topHeadlinesApi.getTopHeadlines(newsApiOrgV2Configuration.apiKey(), country)
                .flatMapIterable(NewsResponse::getArticles)
                .flatMap(article -> getImage(article.getUrlToImage())
                        .map(imageInBytes -> new NewsArticle(
                                article.getTitle(),
                                article.getAuthor(),
                                article.getDescription(),
                                Optional.ofNullable(article.getSource())
                                        .map(NewsSource::getName)
                                        .orElse(""),
                                article.getUrl(),
                                imageInBytes)));
    }

    private Mono<byte[]> getImage(String path) {
        if (path != null && !path.isEmpty()) {
            return WebClient.create(path)
                    .get()
                    .accept(MediaType.IMAGE_JPEG)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .onErrorResume(throwable -> Mono.just(new byte[0]));
        }
        return Mono.just(new byte[0]);
    }
}
