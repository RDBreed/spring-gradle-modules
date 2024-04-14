package eu.phaf.news.wiremockfixture;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class NewsOrgApiFixture {
    private NewsOrgApiFixture() {
        // unused
    }

    public static void success(int wireMockPort) {
        stubFor(get(urlPathMatching("/top-headlines"))
                .withQueryParam("apiKey", equalTo("news_api_key"))
                .willReturn(WireMock.status(200)
                        .withHeader("content-type", "application/json")
                        .withBody(new StringSubstitutor(Map.of("port", wireMockPort))
                                .replace(
                                        FileUtils.readFileToString("wiremock/responses/news-org-api-success.json")
                                )
                        )
                )
        );
    }
}
