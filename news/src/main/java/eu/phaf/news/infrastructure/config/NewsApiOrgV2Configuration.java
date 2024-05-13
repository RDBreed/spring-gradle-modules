package eu.phaf.news.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("news.news-org.api.v2")
public record NewsApiOrgV2Configuration(String apiKey, String basePath) {
}
