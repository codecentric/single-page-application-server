package de.codecentric.spa.server.tests.containers;

import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

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
        String imageName = null;
        try {
            imageName = IMAGE.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return imageName;
    }
}
