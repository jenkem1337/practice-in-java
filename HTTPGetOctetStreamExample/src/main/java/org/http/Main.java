package org.http;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
//        HttpRequest kapRequest = HttpRequest.newBuilder()
//                .uri(new URI("https://www.kap.org.tr/tr/api/home-financial/download-file/4028e4a1422d98690142bceaa31957cf/2025/1"))
//                .GET()
//                .build();
//
//        HttpClient client = HttpClient.newHttpClient();
//        HttpResponse<byte[]> kapResponse = client.send(kapRequest, HttpResponse.BodyHandlers.ofByteArray());
//
//        byte[] rarBytes = kapResponse.body();
//
//        Files.write(Path.of("src/main/resources/enerjisa_bilanco.zip"), rarBytes);

        Path zipFilePath = Path.of("src/main/resources/enerjisa_bilanco.zip");
        ZipFile zipFile = new ZipFile(zipFilePath.toFile());
        ZipEntry entry = zipFile.getEntry("ENJSA_1433660_2025_1.xls");

        if (entry != null) {
            System.out.println("Found file: ENJSA_1433660_2025_1.xls");

            // Open an InputStream for the specified entry
            InputStream stream = zipFile.getInputStream(entry);
            var document = Jsoup.parse(stream, "UTF-8", "www.example.com");
            System.out.println(document.selectXpath("/html/body/h1[1]").text());
//            System.out.println(document.selectXpath("/html/body/div[5]/table[2]/tbody/tr[1]/td[2]").text());
//
//            System.out.print(document.selectXpath("/html/body/div[5]/table[2]/tbody/tr[5]/td[2]").text()+ " : ₺"+ document.selectXpath("/html/body/div[5]/table[2]/tbody/tr[5]/td[4]").text());

            for(int i = 5; i <= 298; i++) {
                if(!document.selectXpath("/html/body/div[5]/table[2]/tbody/tr[%s]/td[4]".formatted(i)).hasText()) continue;
                System.out.println(document.selectXpath("/html/body/div[5]/table[2]/tbody/tr[%s]/td[2]".formatted(i)).text()+ " : ₺"+ document.selectXpath("/html/body/div[5]/table[2]/tbody/tr[%s]/td[4]".formatted(i)).text());

            }
        } else {
            System.out.println("File ENJSA_1433660_2025_1.xls not found in the archive");
        }
        zipFile.close();

    }
}