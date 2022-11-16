package advisor.App;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicReference;

public class Authorization {
    private static final String clientID = "41e5fb02a8a94cb9981e98df582be8d9";
    private static final String redirectUrl = "http://localhost:8372";
    private static final String clientSecret = "5901d3f63c3d4569b042bf469a80f9c0";


    private static String accessURI = "https://accounts.spotify.com";

    public Authorization() {

    }

    public static void setAccessURI(String accessURI) {
        Authorization.accessURI = accessURI;
    }

    @Deprecated
    public static void doAuth() {
        System.out.println("use this link to request the access code:");
        System.out.println(accessURI + "/authorize?client_id=" + clientID + "&redirect_uri=" + redirectUrl + "&response_type=code");
        System.out.println("waiting for code...");
        String answerSpotifyCode = startServerAndGetCodeFromSpotify();
        System.out.println("code received");
        System.out.println("making http request for access_token...");
        String accessToken = getAccessToken(answerSpotifyCode);
        System.out.println("response:");
        System.out.println(accessToken);
    }

    public static String getAuthToken() {
        System.out.println("use this link to request the access code:");
        System.out.println(accessURI + "/authorize?client_id=" + clientID + "&redirect_uri=" + redirectUrl + "&response_type=code");
        System.out.println("waiting for code...");
        String answerSpotifyCode = startServerAndGetCodeFromSpotify();
        System.out.println("code received");
        System.out.println("making http request for access_token...");
        String accessToken = getAccessToken(answerSpotifyCode);
        System.out.println("Success!");
        return accessToken;
    }

    private static String startServerAndGetCodeFromSpotify() {
        AtomicReference<String> answer = new AtomicReference<>("");
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8372), 0);
            server.createContext("/", exchange -> {
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.contains("code")) {
                    answer.set(query.substring(5));
                    query = "Got the code. Return back to your program.";
                } else {
                    query = "Authorization code not found. Try again.";
                }
                exchange.sendResponseHeaders(200, query.length());
                exchange.getResponseBody().write(query.getBytes());
                exchange.getResponseBody().close();
            });
            server.start();
            while (answer.get().isEmpty()) {
                Thread.sleep(10);
            }
            server.stop(10);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return String.valueOf(answer);

    }

    private static String getAccessToken(String answerSpotifyCode) {
        HttpClient httpClient = HttpClient.newHttpClient();

        URI spotifyTokenApiUri = URI.create(accessURI + "/api/token");

        String spotifyData = "grant_type=authorization_code"
                + "&code=" + answerSpotifyCode
                + "&redirect_uri=" + redirectUrl
                + "&client_id=" + clientID
                + "&client_secret=" + clientSecret;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(spotifyTokenApiUri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(spotifyData))
                .build();
        String answerWithToken = "";
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            answerWithToken = response.body();
        } catch (Exception e) {
            System.out.println("We cannot send data. Please, try later.");
        }
        JsonObject answerJson = JsonParser.parseString(answerWithToken).getAsJsonObject();
        return answerJson.get("access_token").getAsString();
    }
}
