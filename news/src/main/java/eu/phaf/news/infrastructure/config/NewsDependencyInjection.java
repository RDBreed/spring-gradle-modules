package eu.phaf.news.infrastructure.config;

import eu.phaf.news.application.gateway.ImageGateway;
import eu.phaf.news.application.service.CountryValidator;
import eu.phaf.news.application.service.NewsService;
import eu.phaf.news.infrastructure.gateway.NewsApiOrgV2Gateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class NewsDependencyInjection {

    @Bean
    public NewsApiOrgV2Gateway newsApiOrgV2Gateway(NewsApiOrgV2Configuration newsApiOrgV2Configuration) {
        return new NewsApiOrgV2Gateway(newsApiOrgV2Configuration, imageGateway());
    }

    @Bean
    public CountryValidator countryValidator() {
        return new CountryValidator();
    }

    @Bean
    public NewsService newsService(NewsApiOrgV2Configuration newsApiOrgV2Configuration) {
        return new NewsService(newsApiOrgV2Gateway(newsApiOrgV2Configuration), countryValidator());
    }

    @Bean
    public ImageGateway imageGateway() {
        return path -> {
            if (path != null && !path.isEmpty()) {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
                return restTemplate.getForObject(path, byte[].class);
            }
            return new byte[0];
        };
    }
}
