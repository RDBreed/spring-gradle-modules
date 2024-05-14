package eu.phaf.news.interfaces.controller;

import eu.phaf.news.application.service.NewsService;
import eu.phaf.news.domain.Error;
import eu.phaf.news.domain.Result;
import eu.phaf.news.infrastructure.exception.InvalidCountryCodeException;
import eu.phaf.openapi.exception.UnauthorizedException;
import eu.phaf.openapi.exception.UnrecoverableClientException;
import eu.phaf.openapi.newsapiv2_0_0.domain.dto.NewsArticleResponse;
import eu.phaf.openapi.newsapiv2_0_0.infrastructure.api.server.NewsApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class NewsController implements NewsApi {
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @Override
    public ResponseEntity<List<NewsArticleResponse>> getNewsByCountry(String country) {
        Result<ResponseEntity<List<NewsArticleResponse>>, Exception> result = newsService.getNewsForCountry(country)
                .map(newsArticles -> newsArticles.stream()
                        .map(newsArticle -> new NewsArticleResponse()
                                .author(newsArticle.author())
                                .description(newsArticle.description())
                                .title(newsArticle.title())
                                .url(newsArticle.url())
                                .sourceName(newsArticle.source())
                                .image(newsArticle.imageInBytes())
                        ).toList())
                .map(ResponseEntity::ok);
        return result.orThrow(this::getErrorResponse);
    }

    private ResponseStatusException getErrorResponse(Error<Exception> exceptionError) {
        return switch (exceptionError.getCode()) {
            case InvalidCountryCodeException ignored ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptionError.getMessage());
            case UnauthorizedException ignored -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unauthorized.");
            case UnrecoverableClientException ignored -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
            default -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong.");
        };
    }
}
