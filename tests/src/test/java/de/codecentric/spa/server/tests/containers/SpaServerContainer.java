package de.codecentric.spa.server.tests.containers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.concurrent.Future;

public class SpaServerContainer extends GenericContainer<SpaServerContainer> {
    public static final int DEFAULT_HTTP_PORT = 80;
    public static final int DEFAULT_HTTPS_PORT = 443;
    public static final String DEFAULT_TLS_CERT = "default.crt";
    public static final String DEFAULT_TLS_KEY = "default.key";

    private static ImageFromDockerfile createImage(Options options) {
        var image = new ImageFromDockerfile()
            .withFileFromClasspath("index.html", options.indexResourcePath)
            .withDockerfileFromBuilder(dockerfileBuilder -> {
                dockerfileBuilder
                    .from(BaseImage.getImageName())
                    .copy("index.html", "./");

                if (options.spaConfigFileName != null) {
                    dockerfileBuilder.copy(options.spaConfigFileName, "./");
                }

                if (options.tlsCertName != null) {
                    dockerfileBuilder.copy(options.tlsCertName, "/etc/ssl/");
                }

                if (options.tlsKeyName != null) {
                    dockerfileBuilder.copy(options.tlsKeyName, "/etc/ssl/");
                }

                for (Map.Entry<String, String> file : options.additionalFiles.entrySet()) {
                    dockerfileBuilder.copy(file.getKey(), "./");
                }
            });

        if (options.spaConfigFileName != null) {
            image.withFileFromClasspath(options.spaConfigFileName, options.spaConfigResourcePath);
        }

        if (options.tlsCertName != null) {
            image.withFileFromClasspath(options.tlsCertName, "test_pki/server.pem");
        }

        if (options.tlsKeyName != null) {
            image.withFileFromClasspath(options.tlsKeyName, "test_pki/server-key.pem");
        }

        for (Map.Entry<String, String> file : options.additionalFiles.entrySet()) {
            image.withFileFromClasspath(file.getKey(), file.getValue());
        }

        return image;
    }

    public SpaServerContainer() {
        this(Options.builder().build());
    }

    public SpaServerContainer(String configResourcePath) {
        this(Options.builder().configResourcePath(configResourcePath).build());
    }

    public SpaServerContainer(Options options) {
        this(createImage(options));

        withExposedPorts(options.exposedPorts);
        waitingFor(Wait.forListeningPort());

        if (options.configResourcePath != null) {
            withClasspathResourceMapping(options.configResourcePath, "/config/config.yaml", BindMode.READ_ONLY);
        }
    }

    private SpaServerContainer(@NonNull Future image) {
        super(image);

        this.setStartupAttempts(5);
    }

    @Value
    @AllArgsConstructor
    @Builder
    public static class Options {
        public String configResourcePath;
        @Builder.Default
        public String indexResourcePath = "simple_spa/index.html";
        public String spaConfigFileName;
        public String spaConfigResourcePath;
        public String tlsCertName;
        public String tlsKeyName;
        @Builder.Default
        public Integer[] exposedPorts = new Integer[] { DEFAULT_HTTP_PORT };
        @Builder.Default
        public Map<String, String> additionalFiles = ImmutableMap.of();
    }
}
