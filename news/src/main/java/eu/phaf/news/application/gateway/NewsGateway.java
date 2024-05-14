package eu.phaf.news.application.gateway;

import eu.phaf.news.domain.Result;
import eu.phaf.news.domain.model.NewsArticle;

import java.util.List;

public interface NewsGateway {
    Result<List<NewsArticle>, Exception> getNewsForCountry(String country);
}
