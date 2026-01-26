package org.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest kapRequest = HttpRequest.newBuilder()
                .uri(new URI("https://www.kap.org.tr/tr/api/home-financial/download-file/4028e4a1422d98690142bceaa31957cf/2025/1"))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<byte[]> kapResponse = client.send(kapRequest, HttpResponse.BodyHandlers.ofByteArray());

        byte[] rarBytes = kapResponse.body();

        Files.write(Path.of("src/main/resources/enerjisa_bilanco.rar"), rarBytes);
    }
}