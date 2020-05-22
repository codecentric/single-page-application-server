package de.codecentric.spa.server.tests.helpers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Http {
    public static HttpResponse get(String url) throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();

        var request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response;
    }
}
