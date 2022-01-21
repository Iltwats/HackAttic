package password_hashing;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class NetworkRequest {
    private final String url = "https://hackattic.com/challenges/password_hashing";
    private final String postUrl = "/solve?access_token=a70414023e5a4ea1";
    private final String getUrl = "/problem?access_token=a70414023e5a4ea1";

    /**
     * Make a post request with content type json
     */
    public void makePostRequest(JSONObject output) throws ExecutionException, InterruptedException {
        // Setting up http clients
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + postUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(output.toString()))
                .build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        response.thenApply(HttpResponse::body);
        String result = response.get().body();
        System.out.println(result);
    }

    /**
     * Make a get request and return the response from json as "image_url"
     */
    public String makeGetRequest() throws ExecutionException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + getUrl))
                .GET()
                .build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        response.thenApply(HttpResponse::body);
        return response.get().body();
    }
}
