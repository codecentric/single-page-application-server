package de.codecentric.spa.server.tests;

import de.codecentric.spa.server.tests.containers.SpaServerContainer;
import de.codecentric.spa.server.tests.helpers.Http;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpTests {
    @Test
    public void portShouldConfigureCustomPort() throws IOException, InterruptedException {
        int customPort = 1234;

        try (var container = new SpaServerContainer(
            SpaServerContainer.Options.builder()
                .configResourcePath("http/custom_port.yaml")
                .exposedPorts(new Integer[] { customPort })
                .build())
        ) {
            container.start();

            var response = Http.get("http://" + container.getHost() + ":" + container.getMappedPort(customPort));

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat((String) response.body()).contains("<base href=\"/\" />");

        }
    }

}
