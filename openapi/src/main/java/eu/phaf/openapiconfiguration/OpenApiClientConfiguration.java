package eu.phaf.openapiconfiguration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.phaf.openapi.config.ApiClient;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.ArrayList;
import java.util.List;

public abstract class OpenApiClientConfiguration {
    protected ApiClient apiClient(OpenApiProperties openApiProperties) {
        var defaultDateFormat = ApiClient.createDefaultDateFormat();
        var defaultObjectMapper = ApiClient.createDefaultObjectMapper(defaultDateFormat);
        defaultObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        WebClient.Builder webClientBuilder = getWebClientBuilder(defaultObjectMapper, openApiProperties);
        if (!openApiProperties.exchangeFilterFunctions().isEmpty()) {
            webClientBuilder.filters(exchangeFilterFunctions -> exchangeFilterFunctions
                    .addAll(openApiProperties.exchangeFilterFunctions()));
        }
        ApiClient apiClient = new ApiClient(webClientBuilder.build());
        apiClient.setBasePath(openApiProperties.basePath());
        return apiClient;
    }

    private WebClient.Builder getWebClientBuilder(ObjectMapper defaultObjectMapper, OpenApiProperties openApiProperties) {
        HttpClient httpClient = HttpClient.create();
        if (openApiProperties.connectionTimeout() != null) {
            httpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, openApiProperties.connectionTimeout());
        }
        if (!openApiProperties.handlers().isEmpty()) {
            httpClient.doOnConnected(connection -> openApiProperties.handlers().forEach(connection::addHandlerLast));
        }


        WebClient.Builder builder = ApiClient.buildWebClientBuilder(defaultObjectMapper)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                    if (clientResponse.statusCode().is5xxServerError()) {
                        return clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("Error occurred with statuscode: " + clientResponse.statusCode())
                                .flatMap(errorBody -> Mono.error(new RetryableException(errorBody)));
                    } else if (clientResponse.statusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
                        return clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("Error occurred with statuscode: " + clientResponse.statusCode())
                                .flatMap(errorBody -> Mono.error(new UnauthorizedException(errorBody)));
                    } else if (clientResponse.statusCode().is4xxClientError()) {
                        return clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("Error occurred with statuscode: " + clientResponse.statusCode())
                                .flatMap(errorBody -> Mono.error(new UnrecoverableClientException(errorBody)));
                    } else {
                        return Mono.just(clientResponse);
                    }
                }));
        if (openApiProperties.maxMemorySizeInBytes() != null) {
            builder.codecs(clientCodecConfigurer -> clientCodecConfigurer
                    .defaultCodecs()
                    .maxInMemorySize(openApiProperties.maxMemorySizeInBytes()));
        }
        return builder;
    }

    public record OpenApiProperties(Integer connectionTimeout,
                                    List<ChannelHandler> handlers,
                                    Integer maxMemorySizeInBytes,
                                    List<ExchangeFilterFunction> exchangeFilterFunctions,
                                    String basePath) {

        public static Builder builder(){
            return new Builder();
        }
        public static class Builder {
            private Integer connectionTimeout;
            private Integer maxMemorySizeInBytes;
            private final List<ChannelHandler> handlers = new ArrayList<>();
            private final List<ExchangeFilterFunction> exchangeFilterFunctions = new ArrayList<>();
            private String basePath;

            public Builder connectionTimeout(Integer connectionTimeout) {
                this.connectionTimeout = connectionTimeout;
                return this;
            }

            public Builder maxMemorySizeInBytes(Integer maxMemorySizeInBytes) {
                this.maxMemorySizeInBytes = maxMemorySizeInBytes;
                return this;
            }

            public Builder addHandler(ChannelHandler handler) {
                this.handlers.add(handler);
                return this;
            }

            public Builder addExchangeFilterFunction(ExchangeFilterFunction exchangeFilterFunction) {
                this.exchangeFilterFunctions.add(exchangeFilterFunction);
                return this;
            }

            public Builder basePath(String basePath) {
                this.basePath = basePath;
                return this;
            }

            public OpenApiProperties build() {
                return new OpenApiProperties(connectionTimeout, handlers, maxMemorySizeInBytes, exchangeFilterFunctions, basePath);
            }
        }
    }
}
