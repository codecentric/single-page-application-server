package de.codecentric.spa.server.tests;

import de.codecentric.spa.server.tests.containers.SpaServerContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Network;

import static de.codecentric.spa.server.tests.containers.Curl.curl;
import static org.assertj.core.api.Assertions.assertThat;

public class Http2Tests {

    @Test
    public void shouldServeHttp11ViaHttpByDefault() {
        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer()
                .withNetwork(network)
                .withNetworkAliases("example.com")
        ) {
            container.start();

            assertThat(curl(network, "curl", "--http2", "-s", "-v", "http://example.com"))
                .contains("HTTP/1.1 200");
        }
    }

    @Test
    public void shouldServeHttp2ViaHttpWithPriorKnowledge() {
        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer("http2/plaintext_http2_enabled.yaml")
                .withNetwork(network)
                .withNetworkAliases("example.com")
        ) {
            container.start();

            // nginx supports http2 over plain text only via prior knowledge
            // https://trac.nginx.org/nginx/ticket/816
            assertThat(curl(network, "curl", "--http2-prior-knowledge", "-s", "-v", "http://example.com"))
                .contains("HTTP/2 200");
        }
    }

    @Test
    public void shouldServeHttp2ByDefaultWithTls() {
        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer(SpaServerContainer.Options.builder()
                .configResourcePath("http2/https_enabled.yaml")
                .tlsCertName(SpaServerContainer.DEFAULT_TLS_CERT)
                .tlsKeyName(SpaServerContainer.DEFAULT_TLS_KEY)
                .build())
                .withNetwork(network)
                .withNetworkAliases("example.com")
        ) {
            container.start();

            assertThat(curl(network, "curl", "--http2", "-s", "-v", "https://example.com"))
                .contains("HTTP/2 200");
        }
    }

    @Test
    public void shouldServeHttp11WithTlsIfHttp2Disabled() {
        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer(SpaServerContainer.Options.builder()
                .configResourcePath("http2/tls_http2_disabled.yaml")
                .tlsCertName(SpaServerContainer.DEFAULT_TLS_CERT)
                .tlsKeyName(SpaServerContainer.DEFAULT_TLS_KEY)
                .build())
                .withNetwork(network)
                .withNetworkAliases("example.com")
        ) {
            container.start();

            assertThat(curl(network, "curl", "--http2", "-s", "-v", "https://example.com"))
                .contains("HTTP/1.1 200 OK");
        }
    }

}
