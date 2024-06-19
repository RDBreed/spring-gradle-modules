package eu.phaf.news;

import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {NewsTestApp.class, TestContainerConfiguration.class})
@ContextConfiguration
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@EnableConfigurationProperties
@ActiveProfiles({"news", "news-feature-test"})
@Tag("feature-test")
public class BaseFeatureApplicationContext {
    @Autowired
    protected WebTestClient webTestClient;
}
