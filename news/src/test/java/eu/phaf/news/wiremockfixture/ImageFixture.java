package eu.phaf.news.wiremockfixture;

import com.github.tomakehurst.wiremock.client.WireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class ImageFixture {

    public static void success() {
        stubFor(get(urlPathMatching("/image"))
                .willReturn(WireMock.status(200)
                        .withBody(FileUtils.readFile("wiremock/responses/img.png"))
                        .withHeader("content-type", "image/jpeg"))
        );
    }
}
