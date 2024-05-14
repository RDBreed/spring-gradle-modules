package eu.phaf.news;

import eu.phaf.news.wiremockfixture.FileUtils;
import eu.phaf.news.wiremockfixture.ImageFixture;
import eu.phaf.news.wiremockfixture.NewsOrgApiFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;

public class NewsFeatureTest extends BaseFeatureApplicationContext {
    @Autowired
    @Value("${wiremock.server.port}")
    private int wireMockPort;

    @Test
    public void shouldGiveNews() {
        // given
        NewsOrgApiFixture.success(wireMockPort);
        ImageFixture.success();
        // when
        String responseBody = webTestClient
                .mutate()
                .exchangeStrategies(ExchangeStrategies
                        .builder()
                        .codecs(codecs -> codecs
                                .defaultCodecs()
                                .maxInMemorySize(1024 * 1024 * 1024))
                        .build())
                .build()
                .get()
                .uri("/news?country=" + "US")
                .exchange()
                // then
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        assertThatJson(json(responseBody))
                .when(IGNORING_ARRAY_ORDER)
                .isEqualTo(json(FileUtils.readFileToString("assertions/responses/news-success.json")));
    }

    @Test
    public void shouldErrorOnBadCountryCode() {
        // given
        NewsOrgApiFixture.success(wireMockPort);
        ImageFixture.success();
        // when
        webTestClient
                .mutate()
                .exchangeStrategies(ExchangeStrategies
                        .builder()
                        .codecs(codecs -> codecs
                                .defaultCodecs()
                                .maxInMemorySize(1024 * 1024 * 1024))
                        .build())
                .build()
                .get()
                .uri("/news?country=" + "XX")
                .exchange()
                // then
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    public void shouldErrorOnClientError() {
        // given
        NewsOrgApiFixture.clientError();
        ImageFixture.success();
        // when
        webTestClient
                .mutate()
                .exchangeStrategies(ExchangeStrategies
                        .builder()
                        .codecs(codecs -> codecs
                                .defaultCodecs()
                                .maxInMemorySize(1024 * 1024 * 1024))
                        .build())
                .build()
                .get()
                .uri("/news?country=" + "US")
                .exchange()
                // then
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    public void shouldErrorOnUnauthorizedError() {
        // given
        NewsOrgApiFixture.unauthorizedError();
        ImageFixture.success();
        // when
        webTestClient
                .mutate()
                .exchangeStrategies(ExchangeStrategies
                        .builder()
                        .codecs(codecs -> codecs
                                .defaultCodecs()
                                .maxInMemorySize(1024 * 1024 * 1024))
                        .build())
                .build()
                .get()
                .uri("/news?country=" + "US")
                .exchange()
                // then
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    public void shouldErrorOnServerError() {
        // given
        NewsOrgApiFixture.serverError();
        ImageFixture.success();
        // when
        webTestClient
                .mutate()
                .exchangeStrategies(ExchangeStrategies
                        .builder()
                        .codecs(codecs -> codecs
                                .defaultCodecs()
                                .maxInMemorySize(1024 * 1024 * 1024))
                        .build())
                .build()
                .get()
                .uri("/news?country=" + "US")
                .exchange()
                // then
                .expectStatus()
                .is5xxServerError();
    }
}
