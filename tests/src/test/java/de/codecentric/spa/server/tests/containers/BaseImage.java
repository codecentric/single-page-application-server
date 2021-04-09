package de.codecentric.spa.server.tests.containers;

import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Paths;

public class BaseImage {
    public static final ImageFromDockerfile IMAGE;

    static {
        var nginxTag = System.getProperty("nginxTag");

        if (nginxTag == null) {
            nginxTag = "stable-alpine";
        }

        IMAGE = new ImageFromDockerfile()
            .withFileFromPath(".", Paths.get(".."))
            .withBuildArg("NGINX_TAG", nginxTag);
    }

    public static String getImageName() {
        return IMAGE.get();
    }
}
