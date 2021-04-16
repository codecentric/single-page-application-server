package de.codecentric.spa.server.tests;

import de.codecentric.spa.server.tests.containers.SpaServerContainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Network;

import static de.codecentric.spa.server.tests.containers.Curl.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class HttpsTests {
    @Test
    public void shouldServeTlsOn8443ByDefaultIfHttpsEnabled() {
        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer(SpaServerContainer.Options.builder()
                .configResourcePath("https/enabled.yaml")
                .tlsCertName(SpaServerContainer.DEFAULT_TLS_CERT)
                .tlsKeyName(SpaServerContainer.DEFAULT_TLS_KEY)
                .build())
                .withNetwork(network)
                .withNetworkAliases("example.com", "www.example.com", "not-included-in-cert.com")
        ) {
            container.start();

            assertCurlLogContains(network, "https://example.com", "<base href=\"/\" />");
            assertCurlLogContains(network, "https://www.example.com", "<base href=\"/\" />");
            assertCurlLogDoesNotContain(network, "https://not-included-in-cert.com", "<base href=\"/\" />");

        }
    }

    @Test
    public void shouldRedirectFromHttpByDefault() {
        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer(SpaServerContainer.Options.builder()
                .configResourcePath("https/enabled.yaml")
                .tlsCertName(SpaServerContainer.DEFAULT_TLS_CERT)
                .tlsKeyName(SpaServerContainer.DEFAULT_TLS_KEY)
                .build())
                .withNetwork(network)
                .withNetworkAliases("example.com", "www.example.com")
        ) {
            container.start();

            String curlLog = curl(network, "curl", "-s", "-v", "http://example.com/some-path");

            assertThat(curlLog).contains("307 Temporary Redirect");
            assertThat(curlLog).contains("Location: https://example.com/some-path");

        }
    }

    @Test
    public void shouldNotOmitPortInRedirectFromHttpIfPortIsNot443() {
        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer(SpaServerContainer.Options.builder()
                .configResourcePath("https/https_redirect_port_1234.yaml")
                .tlsCertName(SpaServerContainer.DEFAULT_TLS_CERT)
                .tlsKeyName(SpaServerContainer.DEFAULT_TLS_KEY)
                .build())
                .withNetwork(network)
                .withNetworkAliases("example.com", "www.example.com")
        ) {
            container.start();

            String curlLog = curl(network, "curl", "-s", "-v", "http://example.com/some-path");

            assertThat(curlLog).contains("307 Temporary Redirect");
            assertThat(curlLog).contains("Location: https://example.com:1234/some-path");

        }
    }

    @Test
    public void shouldOmitPortInRedirectFromHttpIfPortIs443() {
        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer(SpaServerContainer.Options.builder()
                .configResourcePath("https/https_redirect_port_443.yaml")
                .tlsCertName(SpaServerContainer.DEFAULT_TLS_CERT)
                .tlsKeyName(SpaServerContainer.DEFAULT_TLS_KEY)
                .build())
                .withNetwork(network)
                .withNetworkAliases("example.com", "www.example.com")
        ) {
            container.start();

            String curlLog = curl(network, "curl", "-s", "-v", "http://example.com/some-path");

            assertThat(curlLog).contains("307 Temporary Redirect");
            assertThat(curlLog).contains("Location: https://example.com/some-path");

        }
    }

    @Test
    public void shouldServeHstsByDefaultIfHttpsIsEnabled() {
        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer(SpaServerContainer.Options.builder()
                .configResourcePath("https/enabled.yaml")
                .tlsCertName(SpaServerContainer.DEFAULT_TLS_CERT)
                .tlsKeyName(SpaServerContainer.DEFAULT_TLS_KEY)
                .build())
                .withNetwork(network)
                .withNetworkAliases("example.com", "www.example.com")
        ) {
            container.start();

            assertThat(curl(network, "curl", "-s", "-v", "https://example.com"))
                .containsIgnoringCase("strict-transport-security: max-age=31536000");
            assertThat(curl(network, "curl", "-s", "-v", "http://example.com"))
                .containsIgnoringCase("strict-transport-security: max-age=31536000");
        }
    }

    @Test
    public void shouldNotServeHstsIfHstsIsDisabled() {
        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer(SpaServerContainer.Options.builder()
                .configResourcePath("https/disable_hsts.yaml")
                .tlsCertName(SpaServerContainer.DEFAULT_TLS_CERT)
                .tlsKeyName(SpaServerContainer.DEFAULT_TLS_KEY)
                .build())
                .withNetwork(network)
                .withNetworkAliases("example.com", "www.example.com")
        ) {
            container.start();

            assertThat(curl(network, "curl", "-s", "-v", "https://example.com"))
                .doesNotContain("Strict-Transport-Security")
                .doesNotContain("strict-transport-security");
        }
    }

    @Test
    public void shouldNotServeHstsIfHttpsIsDisabled() {
        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer()
                .withNetwork(network)
                .withNetworkAliases("example.com", "www.example.com")
        ) {
            container.start();

            assertThat(curl(network, "curl", "-s", "-v", "https://example.com"))
                .doesNotContain("Strict-Transport-Security")
                .doesNotContain("strict-transport-security");
        }
    }

    @Test
    public void shouldServeTlsOnCustomPort() {
        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer(SpaServerContainer.Options.builder()
                .configResourcePath("https/port_1234.yaml")
                .tlsCertName(SpaServerContainer.DEFAULT_TLS_CERT)
                .tlsKeyName(SpaServerContainer.DEFAULT_TLS_KEY)
                .build())
                .withNetwork(network)
                .withNetworkAliases("example.com", "www.example.com")
        ) {
            container.start();

            assertCurlLogContains(network, "https://example.com:1234", "<base href=\"/\" />");
            assertCurlLogContains(network, "https://www.example.com:1234", "<base href=\"/\" />");
        }
    }

    @Test
    public void shouldServeCertFromCustomLocation() {
        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer(SpaServerContainer.Options.builder()
                .configResourcePath("https/custom_cert_path.yaml")
                .tlsCertName("custom.cert")
                .tlsKeyName("custom.key")
                .build())
                .withNetwork(network)
                .withNetworkAliases("example.com", "www.example.com")
        ) {
            container.start();

            assertCurlLogContains(network, "https://example.com", "<base href=\"/\" />");
            assertCurlLogContains(network, "https://www.example.com", "<base href=\"/\" />");

        }
    }

    @Test
    public void shouldSupportTls13ByDefault() {
        shouldSupportTlsVersion("https/enabled.yaml", "1.3");
    }

    @Test
    public void shouldSupportTls12ByDefault() {
        shouldSupportTlsVersion("https/enabled.yaml", "1.2");
    }

    @Test
    public void shouldNotSupportTls11ByDefault() {
        shouldNotSupportTlsVersion("https/enabled.yaml", "1.1");
    }

    @Test
    public void shouldNotSupportTls10ByDefault() {
        shouldNotSupportTlsVersion("https/enabled.yaml", "1.0");
    }

    @Test
    public void owaspBShouldSupportTls13() {
        shouldSupportTlsVersion("https/owasp_b.yaml", "1.3");
    }

    @Test
    public void owaspBSupportTls12() {
        shouldSupportTlsVersion("https/owasp_b.yaml", "1.2");
    }

    @Test
    public void owaspBShouldNotSupportTls11() {
        shouldNotSupportTlsVersion("https/owasp_b.yaml", "1.1");
    }

    @Test
    public void owaspBShouldNotSupportTls10() {
        shouldNotSupportTlsVersion("https/owasp_b.yaml", "1.0");
    }

    @Test
    public void owaspCShouldSupportTls13() {
        shouldSupportTlsVersion("https/owasp_c.yaml", "1.3");
    }

    @Test
    public void owaspCSupportTls12() {
        shouldSupportTlsVersion("https/owasp_c.yaml", "1.2");
    }

    @Test
    public void owaspCShouldSupportTls11() {
        shouldSupportTlsVersion("https/owasp_c.yaml", "1.1");
    }

    @Test
    public void owaspCShouldSupportTls10() {
        shouldSupportTlsVersion("https/owasp_c.yaml", "1.0", "1 ");
    }

    @Test
    public void owaspDShouldSupportTls13() {
        shouldSupportTlsVersion("https/owasp_d.yaml", "1.3");
    }

    @Test
    public void owaspDSupportTls12() {
        shouldSupportTlsVersion("https/owasp_d.yaml", "1.2");
    }

    @Test
    public void owaspDShouldSupportTls11() {
        shouldSupportTlsVersion("https/owasp_d.yaml", "1.1");
    }

    @Test
    public void owaspDShouldSupportTls10() {
        shouldSupportTlsVersion("https/owasp_d.yaml", "1.0", "1 ");
    }

    private void shouldSupportTlsVersion(String configResourcePath, String version) {
        shouldSupportTlsVersion(configResourcePath, version, version);
    }

    private void shouldSupportTlsVersion(String configResourcePath, String version, String assertVersion) {
        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer(SpaServerContainer.Options.builder()
                .configResourcePath(configResourcePath)
                .tlsCertName(SpaServerContainer.DEFAULT_TLS_CERT)
                .tlsKeyName(SpaServerContainer.DEFAULT_TLS_KEY)
                .build())
                .withNetwork(network)
                .withNetworkAliases("example.com", "www.example.com")
        ) {
            container.start();

            assertThat(curl(network, "curl", "--tls-max", version, "-s", "-v", "https://example.com"))
                .contains("SSL connection using TLSv" + assertVersion);

        }
    }

    private void shouldNotSupportTlsVersion(String configResourcePath, String version) {
        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer(SpaServerContainer.Options.builder()
                .configResourcePath(configResourcePath)
                .tlsCertName(SpaServerContainer.DEFAULT_TLS_CERT)
                .tlsKeyName(SpaServerContainer.DEFAULT_TLS_KEY)
                .build())
                .withNetwork(network)
                .withNetworkAliases("example.com", "www.example.com");
        ) {
            container.start();

            assertThat(curl(network, "curl", "--tls-max", version, "-s", "-v", "https://example.com"))
                .contains("alert protocol version");

        }
    }

}
