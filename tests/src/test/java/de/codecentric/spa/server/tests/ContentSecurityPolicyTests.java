package de.codecentric.spa.server.tests;

import de.codecentric.spa.server.tests.containers.SpaServerContainer;
import de.codecentric.spa.server.tests.helpers.Http;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ContentSecurityPolicyTests {
    @Test
    public void shouldHaveCspByDefault() throws IOException, InterruptedException {
        try (var container = new SpaServerContainer()) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(response.headers().firstValue("Content-Security-Policy")).hasValue(
                "base-uri 'self'; block-all-mixed-content; default-src 'self'; form-action 'self'; frame-ancestors 'self'; frame-src 'self'; object-src 'none'; script-src 'self'; style-src 'self';");
        }
    }

    @Test
    public void shouldAddConnectSrcCspForConfiguredEndpointsByDefault() throws IOException, InterruptedException {
        try (var container = new SpaServerContainer("content_security_policy/config_with_endpoints.yaml")) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(response.headers().firstValue("Content-Security-Policy")).hasValue(
                "base-uri 'self'; block-all-mixed-content; default-src 'self'; form-action 'self'; frame-ancestors 'self'; frame-src 'self'; object-src 'none'; script-src 'self'; style-src 'self'; connect-src https://example.com:1234 http://example.com https://example.com 'self'");
        }
    }

    @Test
    public void shouldNotAddConnectSrcCspForConfiguredEndpointsIfFeatureIsDisabled() throws IOException, InterruptedException {
        try (var container = new SpaServerContainer("content_security_policy/config_with_endpoints_and_disabled_automatic_whitelist_feature.yaml")) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(response.headers().firstValue("Content-Security-Policy")).hasValue(
                "base-uri 'self'; block-all-mixed-content; default-src 'self'; form-action 'self'; frame-ancestors 'self'; frame-src 'self'; object-src 'none'; script-src 'self'; style-src 'self';");
        }
    }

    @Test
    public void shouldNotAddConnectSrcCspForConfiguredEndpointsIfConnectSrcIsConfiguredExplicitly()
        throws IOException, InterruptedException {
        try (var container = new SpaServerContainer("content_security_policy/config_with_endpoints_and_connect_src.yaml")) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(response.headers().firstValue("Content-Security-Policy")).hasValue(
                "base-uri 'self'; block-all-mixed-content; connect-src 'none'; default-src 'self'; form-action 'self'; frame-ancestors 'self'; frame-src 'self'; object-src 'none'; script-src 'self'; style-src 'self';");
        }
    }

    @Test
    public void shouldBeConfigurable() throws IOException, InterruptedException {
        try (var container = new SpaServerContainer("content_security_policy/add_worker_src.yaml")) {
            container.start();

            var response = Http
                .get("http://" + container.getHost() + ":" + container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(response.headers().firstValue("Content-Security-Policy")).hasValue(
                "base-uri 'self'; block-all-mixed-content; default-src 'self'; form-action 'self'; frame-ancestors 'self'; frame-src 'self'; object-src 'none'; script-src 'self'; style-src 'self'; worker-src 'none';");
        }
    }

    @Test
    public void shouldBeAddedToHtml() throws IOException, InterruptedException {
        try (var container = new SpaServerContainer(SpaServerContainer.Options.builder()
            .configResourcePath("content_security_policy/add_worker_src.yaml")
            .additionalFiles(ImmutableMap.of(
                "some_page.html", "content_security_policy/some_page.html"
            ))
            .build()
        )) {
            container.start();

            var response = Http.get(String.format(
                "http://%s:%d/some_page.html",
                container.getHost(),
                container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT)));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(response.headers().firstValue("Content-Security-Policy")).hasValue(
                "base-uri 'self'; block-all-mixed-content; default-src 'self'; form-action 'self'; frame-ancestors 'self'; frame-src 'self'; object-src 'none'; script-src 'self'; style-src 'self'; worker-src 'none';");
            assertThat(response.headers().firstValue("Content-Type")).hasValue("text/html");
        }
    }

    @Test
    public void shouldBeAddedToJs() throws IOException, InterruptedException {
        try (var container = new SpaServerContainer(SpaServerContainer.Options.builder()
            .configResourcePath("content_security_policy/add_worker_src.yaml")
            .additionalFiles(ImmutableMap.of(
                "some_script.js", "content_security_policy/some_script.js"
            ))
            .build()
        )) {
            container.start();

            var response = Http.get(String.format(
                "http://%s:%d/some_script.js",
                container.getHost(),
                container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT)));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(response.headers().firstValue("Content-Security-Policy")).hasValue(
                "base-uri 'self'; block-all-mixed-content; default-src 'self'; form-action 'self'; frame-ancestors 'self'; frame-src 'self'; object-src 'none'; script-src 'self'; style-src 'self'; worker-src 'none';");
            assertThat(response.headers().firstValue("Content-Type")).hasValue("application/javascript");
        }
    }

    @Test
    public void shouldBeAddedToHashedJs() throws IOException, InterruptedException {
        try (var container = new SpaServerContainer(SpaServerContainer.Options.builder()
            .configResourcePath("content_security_policy/add_worker_src.yaml")
            .additionalFiles(ImmutableMap.of(
                "some_script.abc123456.js", "content_security_policy/some_script.js"
            ))
            .build()
        )) {
            container.start();

            var response = Http.get(String.format(
                "http://%s:%d/some_script.abc123456.js",
                container.getHost(),
                container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT)));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(response.headers().firstValue("Content-Security-Policy")).hasValue(
                "base-uri 'self'; block-all-mixed-content; default-src 'self'; form-action 'self'; frame-ancestors 'self'; frame-src 'self'; object-src 'none'; script-src 'self'; style-src 'self'; worker-src 'none';");
            assertThat(response.headers().firstValue("Content-Type")).hasValue("application/javascript");
        }
    }

    @Test
    public void shouldNotBeAddedToCss() throws IOException, InterruptedException {
        try (var container = new SpaServerContainer(SpaServerContainer.Options.builder()
            .configResourcePath("content_security_policy/add_worker_src.yaml")
            .additionalFiles(ImmutableMap.of(
                "some_style.css", "content_security_policy/some_style.css"
            ))
            .build()
        )) {
            container.start();

            var response = Http.get(String.format(
                "http://%s:%d/some_style.css",
                container.getHost(),
                container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT)));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(response.headers().firstValue("Content-Security-Policy")).isEmpty();
            assertThat(response.headers().firstValue("Content-Type")).hasValue("text/css");
        }
    }

    @Test
    public void shouldNotBeAddedToHashedCss() throws IOException, InterruptedException {
        try (var container = new SpaServerContainer(SpaServerContainer.Options.builder()
            .configResourcePath("content_security_policy/add_worker_src.yaml")
            .additionalFiles(ImmutableMap.of(
                "some_style.abc123456.css", "content_security_policy/some_style.css"
            ))
            .build()
        )) {
            container.start();

            var response = Http.get(String.format(
                "http://%s:%d/some_style.abc123456.css",
                container.getHost(),
                container.getMappedPort(SpaServerContainer.DEFAULT_HTTP_PORT)));
            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(response.headers().firstValue("Content-Security-Policy")).isEmpty();
            assertThat(response.headers().firstValue("Content-Type")).hasValue("text/css");
        }
    }
}
