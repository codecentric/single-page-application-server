package de.codecentric.spa.server.tests;

import de.codecentric.spa.server.tests.containers.SpaServerContainer;
import de.codecentric.spa.server.tests.helpers.Http;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class XContentTypeOptionsPolicyTests {
    @Test
    public void shouldBeNoSniffByDefault() throws IOException, InterruptedException {
        try (var container = new SpaServerContainer()) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(response.headers().firstValue("X-Content-Type-Options")).hasValue("nosniff");
        }
    }

    @Test
    public void shouldBeConfigurable() throws IOException, InterruptedException {
        try (var container = new SpaServerContainer("x_content_type_options/not_set.yaml")) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(response.headers().firstValue("X-Content-Type-Options")).isEmpty();
        }
    }
}
