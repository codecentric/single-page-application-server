package de.codecentric.spa.server.tests;

import de.codecentric.spa.server.tests.containers.SpaServerContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Network;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Objects;

import static de.codecentric.spa.server.tests.containers.Curl.curl;
import static org.assertj.core.api.Assertions.assertThat;

public class MiscTests {

    @Test
    public void shouldStartWithTtyAndStdin() {
        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer()
                .withNetwork(network)
                .withNetworkAliases("testcontainer")
                .withCreateContainerCmdModifier(cmd -> cmd.withTty(true).withStdinOpen(true))) {
            container.start();

            assertThat(curl(network, "curl", "http://testcontainer")).contains("<base href=\"/\" />");
        }
    }

    @Test
    public void shouldSupportReadOnlyFileSystemWithConfigOutVolume() {
        try (
            var network = Network.newNetwork();
            var container = new SpaServerContainer()
                .withCreateContainerCmdModifier(cmd -> Objects.requireNonNull(cmd.getHostConfig()).withReadonlyRootfs(true))
                .withNetwork(network)
                .withNetworkAliases("testcontainer")
                .withTmpFs(ImmutableMap.of(
                    "/config/.out", "rw",
                    "/tmp", "rw"
                ))) {
            container.start();

            assertThat(curl(network, "curl", "http://testcontainer")).contains("<base href=\"/\" />");
        }
    }

}
