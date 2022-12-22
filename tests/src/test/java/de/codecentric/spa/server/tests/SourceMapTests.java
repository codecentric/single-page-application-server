package de.codecentric.spa.server.tests;

import de.codecentric.spa.server.tests.containers.SpaServerContainer;
import de.codecentric.spa.server.tests.helpers.Http;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class SourceMapTests {

    @Test
    public void shouldBeDisabledByDefault() throws IOException, InterruptedException {

        try (var container = new SpaServerContainer(SpaServerContainer.Options.builder()
            .additionalFiles(ImmutableMap.of(
                "example.js.map", "source_maps/example.js.map",
                "example.css.map", "source_maps/example.css.map",
                "example.ts", "source_maps/example.ts"
            ))
            .build())) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT) + "/example.js.map");
            assertThat(response.statusCode()).isEqualTo(404);

            response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT) + "/example.css.map");
            assertThat(response.statusCode()).isEqualTo(404);

            response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT) + "/not_existing.css.map");
            assertThat(response.statusCode()).isEqualTo(404);

            // ts extensions are allowed by default
            response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT) + "/example.ts");
            assertThat(response.statusCode()).isEqualTo(200);
        }

    }

    @Test
    public void shouldBeEnabledWithExplicitConfig() throws IOException, InterruptedException {

        try (var container = new SpaServerContainer(SpaServerContainer.Options.builder()
            .configResourcePath("source_maps/enable_source_maps.yaml")
            .additionalFiles(ImmutableMap.of(
                "example.js.map", "source_maps/example.js.map",
                "example.css.map", "source_maps/example.css.map",
                "example.ts", "source_maps/example.ts"
            ))
            .build())) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT) + "/example.js.map");
            assertThat(response.statusCode()).isEqualTo(200);

            response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT) + "/example.css.map");
            assertThat(response.statusCode()).isEqualTo(200);

            response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT) + "/not_existing.js.map");
            assertThat(response.statusCode()).isEqualTo(404);

            response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT) + "/example.ts");
            assertThat(response.statusCode()).isEqualTo(200);
        }

    }

    @Test
    public void regexShouldBeConfigurable() throws IOException, InterruptedException {

        try (var container = new SpaServerContainer(SpaServerContainer.Options.builder()
            .configResourcePath("source_maps/configure_regex.yaml")
            .additionalFiles(ImmutableMap.of(
                "example.js.map", "source_maps/example.js.map",
                "example.css.map", "source_maps/example.css.map",
                "example.ts", "source_maps/example.ts"
            ))
            .build())) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT) + "/example.js.map");
            assertThat(response.statusCode()).isEqualTo(404);

            response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT) + "/example.css.map");
            assertThat(response.statusCode()).isEqualTo(404);

            response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT) + "/example.ts");
            assertThat(response.statusCode()).isEqualTo(404);
        }

    }

}
