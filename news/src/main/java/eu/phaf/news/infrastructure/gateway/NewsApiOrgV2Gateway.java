package eu.phaf.news.infrastructure.gateway;

import eu.phaf.news.application.gateway.ImageGateway;
import eu.phaf.news.application.gateway.NewsGateway;
import eu.phaf.news.domain.model.NewsArticle;
import eu.phaf.news.infrastructure.config.NewsApiOrgV2Configuration;
import eu.phaf.openapi.newsapiv2_0_0.domain.dto.NewsResponse;
import eu.phaf.openapi.newsapiv2_0_0.domain.dto.NewsSource;
import eu.phaf.openapi.newsapiv2_0_0.infrastructure.api.client.TopHeadlinesApi;
import eu.phaf.openapiconfiguration.OpenApiClientConfiguration;
import reactor.core.publisher.Flux;

import java.util.Optional;

public class NewsApiOrgV2Gateway implements NewsGateway {
    private final TopHeadlinesApi topHeadlinesApi;
    private final NewsApiOrgV2Configuration newsApiOrgV2Configuration;
    private final ImageGateway imageGateway;

    public NewsApiOrgV2Gateway(NewsApiOrgV2Configuration newsApiOrgV2Configuration, ImageGateway imageGateway) {
        this.newsApiOrgV2Configuration = newsApiOrgV2Configuration;
        this.topHeadlinesApi = new TopHeadlinesApi(
                OpenApiClientConfiguration.apiClient(OpenApiClientConfiguration.OpenApiProperties
                        .builder()
                        .basePath(newsApiOrgV2Configuration.basePath())
                        .build()));
        this.imageGateway = imageGateway;
    }

    @Override
    public Flux<NewsArticle> getNewsForCountry(String country) {
        return topHeadlinesApi.getTopHeadlines(newsApiOrgV2Configuration.apiKey(), country)
                .flatMapIterable(NewsResponse::getArticles)
                .flatMap(article -> imageGateway.getImage(article.getUrlToImage())
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
}
