package de.codecentric.spa.server.tests;

import de.codecentric.spa.server.tests.containers.SpaServerContainer;
import de.codecentric.spa.server.tests.helpers.Http;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Network;

import java.io.IOException;

import static de.codecentric.spa.server.tests.containers.Curl.assertCurlLogContains;
import static de.codecentric.spa.server.tests.containers.Curl.curl;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class BaseHrefTests {

    @Test
    public void shouldSetDefaultBaseHrefIfNotConfigured() throws IOException, InterruptedException {

        try (var container = new SpaServerContainer()) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat((String) response.body()).contains("<base href=\"/\" />");
        }

    }

    @Test
    public void anyHostShouldServeDefaultBaseHref() throws IOException, InterruptedException {

        try (var container = new SpaServerContainer("base_href/multiple_servers_with_different_base_hrefs.yaml")) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat((String) response.body()).contains("<base href=\"/default-base-path/\" />");

        }

    }

    @Test
    public void configWithExplicitServerNamesShouldServeSpecialBaseHref() {

        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer("base_href/multiple_servers_with_different_base_hrefs.yaml")
                .withNetwork(network)
                .withNetworkAliases("secondary-server.com", "www.secondary-server.com");
        ) {
            container.start();

            assertCurlLogContains(network, "http://secondary-server.com",
                "<base href=\"/secondary-server-base-path/\" />");
            assertCurlLogContains(network, "http://www.secondary-server.com",
                "<base href=\"/secondary-server-base-path/\" />");
        }

    }

    @Test
    public void shouldServeBaseElementAtTheTopOfTheHeadSection() throws IOException, InterruptedException {

        try (
            var container = new SpaServerContainer(SpaServerContainer.Options.builder()
                .indexResourcePath("base_href/index_with_link_at_the_top.html")
                .build());
        ) {
            container.start();

            var response = Http.get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT));
            assertThat(response.statusCode()).isEqualTo(200);

            var body = ((String)response.body());

            assertThat(body.indexOf("<base href=\"/\" />"))
                .isLessThan(body.indexOf("<link"))
                .isGreaterThan(body.indexOf("<head>"))
                .isGreaterThanOrEqualTo(0);
        }

    }
}
