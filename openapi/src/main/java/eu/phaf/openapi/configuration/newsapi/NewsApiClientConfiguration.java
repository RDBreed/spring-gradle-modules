package eu.phaf.openapi.configuration.newsapi;

import eu.phaf.openapi.configuration.OpenApiClientConfiguration.OpenApiProperties;
import eu.phaf.openapi.exception.RetryableException;
import eu.phaf.openapi.exception.UnauthorizedException;
import eu.phaf.openapi.exception.UnrecoverableClientException;
import eu.phaf.openapi.newsapiv2_0_0.infrastructure.config.nonreactive.ApiClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;


public class NewsApiClientConfiguration {

    public static ApiClient apiClient(OpenApiProperties openApiProperties) {
        RestTemplate restTemplate = new RestTemplate();
        // This allows us to read the response more than once - Necessary for debugging.
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()));

        // disable default URL encoding
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        restTemplate.setUriTemplateHandler(uriBuilderFactory);
        restTemplate.setInterceptors(Stream.of(
                        Collections.singletonList(exceptionInterceptor()),
                        restTemplate.getInterceptors(),
                        openApiProperties.requestInterceptors())
                .flatMap(Collection::stream)
                .toList());
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(openApiProperties.basePath());
        return apiClient;
    }

    private static ClientHttpRequestInterceptor exceptionInterceptor() {
        return (request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request, body);
            if (response.getStatusCode().is5xxServerError()) {
                throw new RetryableException(response.getStatusText());
            } else if (response.getStatusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
                throw new UnauthorizedException(response.getStatusText());
            } else if (response.getStatusCode().is4xxClientError()) {
                throw new UnrecoverableClientException(response.getStatusText());
            }
            return response;
        };
    }
}
