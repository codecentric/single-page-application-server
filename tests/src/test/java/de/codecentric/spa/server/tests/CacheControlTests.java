package de.codecentric.spa.server.tests;

import de.codecentric.spa.server.tests.containers.SpaServerContainer;
import de.codecentric.spa.server.tests.helpers.Http;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class CacheControlTests {
    @Test
    public void hashedJsShouldBeImmutable() throws IOException, InterruptedException {
        shouldBeImmutable("abcdef1234567890.js", "application/javascript");
    }

    @Test
    public void simpleJsShouldNotBeImmutable() throws IOException, InterruptedException {
        shouldNotBeImmutable("js", "application/javascript");
    }

    @Test
    public void hashedCssShouldBeImmutable() throws IOException, InterruptedException {
        shouldBeImmutable("abcdef1234567890.css", "text/css");
    }

    @Test
    public void simpleCssShouldNotBeImmutable() throws IOException, InterruptedException {
        shouldNotBeImmutable("css", "text/css");
    }

    @Test
    public void hashedPngShouldBeImmutable() throws IOException, InterruptedException {
        shouldBeImmutable("abcdef1234567890.png", "image/png");
    }

    @Test
    public void simplePngShouldNotBeImmutable() throws IOException, InterruptedException {
        shouldNotBeImmutable("png", "image/png");
    }

    /**
     * Hashed html should not be immutable as we might want to modify the page, but the links must keep stable.
     */
    @Test
    public void hashedHtmlShouldNotBeImmutable() throws IOException, InterruptedException {
        shouldNotBeImmutable("abcdef1234567890.html", "text/html");
        shouldNotBeImmutable("abcdef1234567890.htm", "text/html");
    }

    @Test
    public void simpleHtmlShouldNotBeImmutable() throws IOException, InterruptedException {
        shouldNotBeImmutable("html", "text/html");
        shouldNotBeImmutable("htm", "text/html");
    }

    @Test
    public void defaultPageShouldNotBeImmutable() throws IOException, InterruptedException {
        try (var container = new SpaServerContainer()) {
            container.start();

            var response = Http.get(String.format(
                "http://%s:%d/some-page",
                container.getHost(),
                container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT)));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(response.headers().firstValue("cache-control")).isEmpty();
            assertThat(response.headers().firstValue("etag")).isNotEmpty();
            assertThat(response.headers().firstValue("content-type")).hasValue("text/html");
        }
    }

    private void shouldBeImmutable(String extension, String mimeType) throws IOException, InterruptedException {
        try (var container = new SpaServerContainer(SpaServerContainer.Options.builder()
            .additionalFiles(ImmutableMap.of(
                "file." + extension, "cache_control/some_file"
            ))
            .build()
        )) {
            container.start();

            var response = Http.get(String.format(
                "http://%s:%d/file." + extension,
                container.getHost(),
                container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT)));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(response.headers().firstValue("cache-control")).hasValue("public, max-age=31536000, immutable");
            assertThat(response.headers().firstValue("etag")).isEmpty();
            assertThat(response.headers().firstValue("content-type")).hasValue(mimeType);
        }
    }

    private void shouldNotBeImmutable(String extension, String mimeType) throws IOException, InterruptedException {
        try (var container = new SpaServerContainer(SpaServerContainer.Options.builder()
            .additionalFiles(ImmutableMap.of(
                "file." + extension, "cache_control/some_file"
            ))
            .build()
        )) {
            container.start();

            var response = Http.get(String.format(
                "http://%s:%d/file." + extension,
                container.getHost(),
                container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT)));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(response.headers().firstValue("cache-control")).isEmpty();
            assertThat(response.headers().firstValue("etag")).isNotEmpty();
            assertThat(response.headers().firstValue("content-type")).hasValue(mimeType);
        }
    }
}
