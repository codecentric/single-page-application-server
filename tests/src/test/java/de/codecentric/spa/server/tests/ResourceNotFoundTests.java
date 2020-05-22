package de.codecentric.spa.server.tests;

import de.codecentric.spa.server.tests.containers.SpaServerContainer;
import de.codecentric.spa.server.tests.helpers.Http;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceNotFoundTests {

    @Test
    public void jsShouldNotDefaultToIndex() throws IOException, InterruptedException {
        shouldNotDefaultToIndex("js");
    }

    @Test
    public void cssShouldNotDefaultToIndex() throws IOException, InterruptedException {
        shouldNotDefaultToIndex("css");
    }

    @Test
    public void pngShouldNotDefaultToIndex() throws IOException, InterruptedException {
        shouldNotDefaultToIndex("png");
    }

    @Test
    public void unknownExtensionsShouldDefaultToIndex() throws IOException, InterruptedException {
        try (var container = new SpaServerContainer()) {
            container.start();

            var response = Http.get(String.format(
                "http://%s:%d/file.unknown",
                container.getHost(),
                container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT)));
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.headers().firstValue("Content-Type")).hasValue("text/html");
        }
    }

    private void shouldNotDefaultToIndex(String extension) throws IOException, InterruptedException {
        try (var container = new SpaServerContainer()) {
            container.start();

            var response = Http.get(String.format(
                "http://%s:%d/file." + extension,
                container.getHost(),
                container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT)));
            assertThat(response.statusCode()).isEqualTo(404);
        }
    }
}
