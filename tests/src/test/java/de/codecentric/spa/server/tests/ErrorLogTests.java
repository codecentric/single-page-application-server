package de.codecentric.spa.server.tests;

import de.codecentric.spa.server.tests.containers.SpaServerContainer;
import de.codecentric.spa.server.tests.helpers.Http;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ErrorLogTests {

    @Test
    public void shouldBeEnabledByDefault() throws IOException, InterruptedException {

        try (var container = new SpaServerContainer()) {
            container.start();

            var response = Http.get(String.format(
                "http://%s:%d/non_existing.js", container.getHost(),
                container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT)));

            assertThat(response.statusCode()).isEqualTo(404);
            assertThat(container.getLogs()).containsPattern("\\[error\\].*non_existing\\.js");
        }

    }
}
