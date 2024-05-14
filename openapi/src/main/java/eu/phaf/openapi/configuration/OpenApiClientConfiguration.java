package eu.phaf.openapi.configuration;

import io.netty.channel.ChannelHandler;
import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.util.ArrayList;
import java.util.List;

public class OpenApiClientConfiguration {
    public record OpenApiProperties(Integer connectionTimeout,
                                    List<ChannelHandler> handlers,
                                    Integer maxMemorySizeInBytes,
                                    List<ClientHttpRequestInterceptor> requestInterceptors,
                                    String basePath) {

        public static OpenApiProperties.Builder builder() {
            return new OpenApiProperties.Builder();
        }

        public static class Builder {
            private Integer connectionTimeout;
            private List<ChannelHandler> handlers = new ArrayList<>();
            private Integer maxMemorySizeInBytes;
            private List<ClientHttpRequestInterceptor> exchangeFilterFunctions = new ArrayList<>();
            private String basePath;

            // TODO builders...

            public OpenApiProperties.Builder basePath(String basePath) {
                this.basePath = basePath;
                return this;
            }

            public OpenApiProperties build() {
                return new OpenApiProperties(connectionTimeout, handlers, maxMemorySizeInBytes, exchangeFilterFunctions, basePath);
            }
        }
    }
}
