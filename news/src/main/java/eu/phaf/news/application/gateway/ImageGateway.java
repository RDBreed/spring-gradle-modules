package eu.phaf.news.application.gateway;

import reactor.core.publisher.Mono;

public interface ImageGateway {
    Mono<byte[]> getImage(String path);
}
