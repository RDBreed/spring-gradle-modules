package eu.phaf.news.adapters.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("news.news-org.api.v2")
public record NewsApiOrgV2Configuration(String apiKey, String basePath) {
}
