package de.codecentric.spa.server.tests.containers;

import de.codecentric.spa.server.tests.helpers.ContainerStoppedStartupCheckStrategy;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.images.builder.ImageFromDockerfile;

import static org.assertj.core.api.Assertions.assertThat;

public class Curl {
    public static GenericContainer container() {
        return new GenericContainer(new ImageFromDockerfile()
            .withFileFromClasspath("test-ca.pem", "test_pki/ca.pem")
            .withDockerfileFromBuilder(dockerfileBuilder -> dockerfileBuilder
                .from("alpine")
                .run("apk --no-cache add curl ca-certificates")
                .copy("test-ca.pem", "/usr/local/share/ca-certificates/")
                .run("update-ca-certificates")
            )
        );
    }

    public static void assertCurlLogContains(Network network, String url, String expectedContent) {
        String log = curl(network, "curl", "-s", url);
        assertThat(log).contains(expectedContent);
    }

    public static void assertCurlLogDoesNotContain(Network network, String url, String unexpectedContent) {
        String log = curl(network, "curl", "-s", url);
        assertThat(log).doesNotContain(unexpectedContent);
    }

    /**
     * @return container log
     */
    public static String curl(Network network, String ...command) {
        try (var curl = container()
            .withNetwork(network)
            .withCommand(command)
            .withStartupCheckStrategy(new ContainerStoppedStartupCheckStrategy())) {
            curl.start();

            return curl.getLogs();
        }
    }
}
