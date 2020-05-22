package de.codecentric.spa.server.tests;

import de.codecentric.spa.server.tests.containers.SpaServerContainer;
import de.codecentric.spa.server.tests.helpers.Http;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Network;

import java.io.IOException;

import static de.codecentric.spa.server.tests.containers.Curl.assertCurlLogContains;
import static org.assertj.core.api.Assertions.assertThat;

public class SpaConfigTests {
    @Test
    public void shouldReplaceSpaConfigWithHash() throws IOException, InterruptedException {
        testDefaultConfigWith("spa_config/index_with_spa_config_with_hash.html", "spa_config.a4f8b234.js");
    }

    @Test
    public void shouldReplaceSpaConfigWithoutHash() throws IOException, InterruptedException {
        testDefaultConfigWith("spa_config/index_with_spa_config_without_hash.html", "spa_config.js");
    }

    @Test
    public void shouldReplaceSpaConfigWithoutUnderscore() throws IOException, InterruptedException {
        testDefaultConfigWith("spa_config/index_with_spaconfig_without_underscore.html", "spaConfig.js");
    }

    private void testDefaultConfigWith(String indexResourcePath, String initialSpaConfigFileName)
        throws IOException, InterruptedException {
        try (var container = new SpaServerContainer(
            SpaServerContainer.Options.builder()
                .configResourcePath("spa_config/add_spa_config.yaml")
                .indexResourcePath(indexResourcePath)
                .spaConfigFileName(initialSpaConfigFileName)
                .spaConfigResourcePath("spa_config/spa_config.js")
                .build()
        )) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat((String) response.body()).contains(
                "<script type=\"text/javascript\" src=\"./spa_config.912324774c894b8d2af422094b696a74684e7747.js\"></script>"
            );

            response = Http.get(String.format(
                "http://%s:%d/spa_config.912324774c894b8d2af422094b696a74684e7747.js",
                container.getHost(),
                container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT)
            ));

            assertThat((String) response.body()).isEqualTo(
                "var spaConfig = {\"defaultProperty\":\"This is not overridden\",\"endpoints\":{},\"testProperty\":\"Default Value\"}"
            );

        }
    }

    @Test
    public void configWithExplicitServerNamesShouldServeSeparateIndexFile() {

        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer(
                SpaServerContainer.Options.builder()
                    .configResourcePath("spa_config/add_spa_config.yaml")
                    .indexResourcePath("spa_config/index_with_spa_config_without_hash.html")
                    .spaConfigFileName("spa_config.js")
                    .spaConfigResourcePath("spa_config/spa_config.js")
                    .build())
                .withNetwork(network)
                .withNetworkAliases("secondary-server.com", "www.secondary-server.com");
        ) {
            container.start();

            assertCurlLogContains(network, "http://secondary-server.com",
                "<script type=\"text/javascript\" src=\"./spa_config.8f066938e2c25bff04e88e979e97cb8b8dd092bc.js\"></script>");

            assertCurlLogContains(network,
                "http://secondary-server.com/spa_config.8f066938e2c25bff04e88e979e97cb8b8dd092bc.js",
                "var spaConfig = {\"defaultProperty\":\"This is not overridden\",\"endpoints\":{},\"testProperty\":\"Secondary Server Value\"}");

            assertCurlLogContains(network, "http://www.secondary-server.com",
                "<script type=\"text/javascript\" src=\"./spa_config.8f066938e2c25bff04e88e979e97cb8b8dd092bc.js\"></script>");

            assertCurlLogContains(network,
                "http://www.secondary-server.com/spa_config.8f066938e2c25bff04e88e979e97cb8b8dd092bc.js",
                "var spaConfig = {\"defaultProperty\":\"This is not overridden\",\"endpoints\":{},\"testProperty\":\"Secondary Server Value\"}");
        }

    }
}
