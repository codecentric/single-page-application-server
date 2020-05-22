package de.codecentric.spa.server.tests;

import de.codecentric.spa.server.tests.containers.SpaServerContainer;
import de.codecentric.spa.server.tests.helpers.Http;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class AccessLogTests {

    @Test
    public void shouldBeDisabledByDefault() throws IOException, InterruptedException {

        try (var container = new SpaServerContainer()) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(container.getLogs()).doesNotContain("GET / ");
        }

    }

    @Test
    public void shouldBeEnabledWithExplicitConfig() throws IOException, InterruptedException {

        try (var container = new SpaServerContainer("access_log/enable_access_log.yaml")) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(container.getLogs()).contains("\"GET / HTTP/1.1\" 200");

        }

    }

}
