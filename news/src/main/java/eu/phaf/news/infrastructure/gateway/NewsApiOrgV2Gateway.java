package eu.phaf.news.infrastructure.gateway;

import eu.phaf.news.application.gateway.ImageGateway;
import eu.phaf.news.application.gateway.NewsGateway;
import eu.phaf.news.domain.Result;
import eu.phaf.news.domain.TrySupplier;
import eu.phaf.news.domain.model.NewsArticle;
import eu.phaf.news.infrastructure.config.NewsApiOrgV2Configuration;
import eu.phaf.openapi.configuration.OpenApiClientConfiguration.OpenApiProperties;
import eu.phaf.openapi.configuration.newsapi.NewsApiClientConfiguration;
import eu.phaf.openapi.newsapiv2_0_0.domain.dto.NewsResponse;
import eu.phaf.openapi.newsapiv2_0_0.domain.dto.NewsSource;
import eu.phaf.openapi.newsapiv2_0_0.infrastructure.api.client.TopHeadlinesApi;

import java.util.List;
import java.util.Optional;

public class NewsApiOrgV2Gateway implements NewsGateway {
    private final TopHeadlinesApi topHeadlinesApi;
    private final NewsApiOrgV2Configuration newsApiOrgV2Configuration;
    private final ImageGateway imageGateway;

    public NewsApiOrgV2Gateway(NewsApiOrgV2Configuration newsApiOrgV2Configuration, ImageGateway imageGateway) {
        this.newsApiOrgV2Configuration = newsApiOrgV2Configuration;
        this.topHeadlinesApi = new TopHeadlinesApi(
                NewsApiClientConfiguration.apiClient(OpenApiProperties
                        .builder()
                        .basePath(newsApiOrgV2Configuration.basePath())
                        .build()));
        this.imageGateway = imageGateway;
    }

    @Override
    public Result<List<NewsArticle>, Exception> getNewsForCountry(String country) {
        return new TrySupplier().get(() -> {
                    NewsResponse topHeadlines = topHeadlinesApi.getTopHeadlines(newsApiOrgV2Configuration.apiKey(), country);
                    return topHeadlines.getArticles()
                            .stream()
                            .map(article ->
                                    new NewsArticle(
                                            article.getTitle(),
                                            article.getAuthor(),
                                            article.getDescription(),
                                            Optional.ofNullable(article.getSource())
                                                    .map(NewsSource::getName)
                                                    .orElse(""),
                                            article.getUrl(),
                                            imageGateway.getImage(article.getUrlToImage()))
                            )
                            .toList();
                }
        );
    }
}
