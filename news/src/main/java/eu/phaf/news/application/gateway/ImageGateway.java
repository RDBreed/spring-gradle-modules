package eu.phaf.news.application.gateway;

import reactor.core.publisher.Mono;

public interface ImageGateway {
    byte[] getImage(String path);
}
