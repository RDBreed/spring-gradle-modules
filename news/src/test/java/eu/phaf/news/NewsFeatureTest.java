package eu.phaf.news;

import eu.phaf.news.application.service.CountryValidator;
import eu.phaf.news.application.service.NewsService;
import eu.phaf.news.wiremockfixture.FileUtils;
import eu.phaf.news.wiremockfixture.ImageFixture;
import eu.phaf.news.wiremockfixture.NewsOrgApiFixture;
import eu.phaf.stateman.retry.RetryTaskActionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.time.Duration;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class NewsFeatureTest extends BaseFeatureApplicationContext {
    @Autowired
    @Value("${wiremock.server.port}")
    private int wireMockPort;

    @Autowired
    private RetryTaskActionRepository retryTaskActionRepository;

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

                .responseTimeout(Duration.ofDays(3))

                .build()
                .get()
                .uri("/news?country=" + "XX")
                .exchange()
                // then
                .expectStatus()
                // TODO exception handling
                .is5xxServerError();

        await()
                .atMost(Duration.ofSeconds(20))
                .pollDelay(Duration.ofSeconds(15))
                .untilAsserted(() -> assertThat(retryTaskActionRepository.count(NewsService.class)).isZero());
    }
}
