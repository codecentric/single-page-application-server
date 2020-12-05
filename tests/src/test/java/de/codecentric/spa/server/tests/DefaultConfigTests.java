package de.codecentric.spa.server.tests;

import de.codecentric.spa.server.tests.containers.SpaServerContainer;
import de.codecentric.spa.server.tests.helpers.Http;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class DefaultConfigTests {

    @Test
    public void shouldBeAppliedWithLeastPriority() throws IOException, InterruptedException {

        try (var container = new SpaServerContainer(SpaServerContainer.Options.builder()
            .indexResourcePath("spa_config/index_with_spa_config_without_hash.html")
            .defaultConfigResourcePath("default_config/default.yaml")
            .configResourcePath("default_config/config.yaml")
            .build())) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat((String) response.body()).contains(
                "<script type=\"text/javascript\" src=\"./spa_config.01c639aa476ec270940bf7d3ebdab1de56a79fdc.js\"></script>"
            );

            response = Http.get(String.format(
                "http://%s:%d/spa_config.01c639aa476ec270940bf7d3ebdab1de56a79fdc.js",
                container.getHost(),
                container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT)
            ));

            assertThat((String) response.body()).isEqualTo(
                "var spaConfig = {\"defaultProperty\":\"This is not overridden\",\"endpoints\":{},\"testProperty\":\"Overridden Value\"}"
            );
        }

    }

    @Test
    public void shouldBeAppliedWhenConfigFilesEnvironmentVariableIsSet() throws IOException, InterruptedException {

        try (var container = new SpaServerContainer(SpaServerContainer.Options.builder()
            .indexResourcePath("spa_config/index_with_spa_config_without_hash.html")
            .defaultConfigResourcePath("default_config/default.yaml")
            .additionalConfigResources(ImmutableMap.of("/config/custom.yaml", "default_config/config.yaml"))
            .build())) {
            container.addEnv("CONFIG_FILES", "file:///config/custom.yaml");
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat((String) response.body()).contains(
                "<script type=\"text/javascript\" src=\"./spa_config.01c639aa476ec270940bf7d3ebdab1de56a79fdc.js\"></script>"
            );

            response = Http.get(String.format(
                "http://%s:%d/spa_config.01c639aa476ec270940bf7d3ebdab1de56a79fdc.js",
                container.getHost(),
                container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT)
            ));

            assertThat((String) response.body()).isEqualTo(
                "var spaConfig = {\"defaultProperty\":\"This is not overridden\",\"endpoints\":{},\"testProperty\":\"Overridden Value\"}"
            );
        }

    }

}