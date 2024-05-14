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

    public static void clientError() {
        stubFor(get(urlPathMatching("/top-headlines"))
                .withQueryParam("apiKey", equalTo("news_api_key"))
                .willReturn(WireMock.status(404)
                        .withHeader("content-type", "application/json")
                )
        );
    }

    public static void unauthorizedError() {
        stubFor(get(urlPathMatching("/top-headlines"))
                .withQueryParam("apiKey", equalTo("news_api_key"))
                .willReturn(WireMock.status(401)
                        .withHeader("content-type", "application/json")
                )
        );
    }

    public static void serverError() {
        stubFor(get(urlPathMatching("/top-headlines"))
                .withQueryParam("apiKey", equalTo("news_api_key"))
                .willReturn(WireMock.status(501)
                        .withHeader("content-type", "application/json")
                )
        );
    }
}
