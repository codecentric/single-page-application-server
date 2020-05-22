package de.codecentric.spa.server.tests;

import de.codecentric.spa.server.tests.containers.SpaServerContainer;
import de.codecentric.spa.server.tests.helpers.Http;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ReferrerPolicyTests {
    @Test
    public void shouldHaveNoReferrerByDefault() throws IOException, InterruptedException {
        try (var container = new SpaServerContainer()) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(response.headers().firstValue("Referrer-Policy")).hasValue("no-referrer");
        }
    }

    @Test
    public void shouldBeConfigurable() throws IOException, InterruptedException {
        try (var container = new SpaServerContainer("referrer_policy/origin.yaml")) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(response.headers().firstValue("Referrer-Policy")).hasValue("origin");
        }
    }
}
