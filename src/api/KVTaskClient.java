package api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final int port = 8078;
    public HttpClient httpClient;
    private final URI url;
    private String apiToken;

    public KVTaskClient(String host) {
        httpClient = HttpClient.newHttpClient();
        this.url = URI.create(host);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(host + ":" + port + "/register"))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                this.apiToken = response.body();
            } else {
                System.out.println("Ошибка регистрации\n" + "Код ошибки - " + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Ошибка регистрации");
        }
    }

    public void put(String key, String json) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(url + "/save" + key + "?API_TOKEN=" + apiToken))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Состояние менеджера задач успешно сохранено");
            } else {
                System.out.println("Ошибка сохранения состояния менеджера\n" + "Код ошибки - " + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("Ошибка сохранения");
        }
    }

    public String load(String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url + "/load" + key + "?API_TOKEN=" + apiToken))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("Ошибка загрузки состояния менеджера\n" + "Код ошибки - " + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("Ошибка сохранения");
        }
        return null;
    }
}
