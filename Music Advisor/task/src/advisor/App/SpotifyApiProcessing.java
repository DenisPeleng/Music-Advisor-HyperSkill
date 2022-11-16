package advisor.App;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpotifyApiProcessing {
    private static String apiURI = "https://api.spotify.com";

    public static void setApiURI(String accessURI) {
        SpotifyApiProcessing.apiURI = accessURI;
    }

    private static String sendRequest(String requestUrl) {
        String accessToken = MusicAdviserApp.getAccessToken();
        HttpClient httpClient = HttpClient.newHttpClient();

        URI spotifyTokenApiUri = URI.create(requestUrl);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(spotifyTokenApiUri)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        String answerWithJson = "";
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            answerWithJson = response.body();
        } catch (Exception e) {
            System.out.println("We cannot send data. Please, try later.");
        }
        if (answerWithJson.contains("error")) {
            JsonObject json = JsonParser.parseString(answerWithJson).getAsJsonObject();
            JsonObject error = json.getAsJsonObject("error");
            System.out.println(error.get("message").getAsString());
            return "error";
        }
        return answerWithJson;
    }

    public static List<String> getNew() {
        List<String> outputInfo = new ArrayList<>();
        String getNewUrl = apiURI + "/v1/browse/new-releases";
        String answerFromServer = sendRequest(getNewUrl);
        if (answerFromServer.contains("error")) {
            return outputInfo;
        }
        JsonObject newReleases = JsonParser.parseString(answerFromServer).getAsJsonObject().get("albums").getAsJsonObject();
        for (JsonElement currentAlbum : newReleases.getAsJsonArray("items")) {
            String currentAlbumStr = currentAlbum.getAsJsonObject().get("name").getAsString();
            List<String> artists = new ArrayList<>();
            for (JsonElement currentArtist : currentAlbum.getAsJsonObject().get("artists").getAsJsonArray()
            ) {
                artists.add(currentArtist.getAsJsonObject().get("name").getAsString());
            }
            String artistsStr = Arrays.toString(artists.toArray());
            String externalLinkStr = currentAlbum.getAsJsonObject().get("external_urls").getAsJsonObject().get("spotify").getAsString();
            outputInfo.add(currentAlbumStr + "\n" + artistsStr + "\n" + externalLinkStr);
        }
        return outputInfo;
    }

    public static List<String> getFeatured() {
        List<String> outputInfo = new ArrayList<>();
        String getNewUrl = apiURI + "/v1/browse/featured-playlists";
        String answerFromServer = sendRequest(getNewUrl);
        if (answerFromServer.contains("error")) {
            return outputInfo;
        }
        JsonArray featuredPlayLists = JsonParser.parseString(answerFromServer).getAsJsonObject().get("playlists").getAsJsonObject().get("items").getAsJsonArray();
        for (JsonElement currentPlaylist : featuredPlayLists
        ) {
            String namePlaylist = currentPlaylist.getAsJsonObject().get("name").getAsString();
            String externalLinkUrl = currentPlaylist.getAsJsonObject().get("external_urls").getAsJsonObject().get("spotify").getAsString();
            outputInfo.add(namePlaylist + "\n" + externalLinkUrl);
        }
        return outputInfo;
    }

    public static List<String> getCategories() {
        List<String> outputInfo = new ArrayList<>();
        String getNewUrl = apiURI + "/v1/browse/categories";
        String answerFromServer = sendRequest(getNewUrl);
        if (answerFromServer.contains("error")) {
            return outputInfo;
        }
        JsonArray categories = JsonParser.parseString(answerFromServer).getAsJsonObject().get("categories").getAsJsonObject().get("items").getAsJsonArray();
        for (JsonElement currentCategory : categories
        ) {
            outputInfo.add(currentCategory.getAsJsonObject().get("name").getAsString());
        }
        return outputInfo;
    }

    public static List<String> getPlayLists(String category) {
        List<String> outputInfo = new ArrayList<>();
        String categoryId = getCategoryId(category);
        if (categoryId.contains("unknown")) {
            System.out.println("Unknown category name.");
            return outputInfo;
        }
        String getNewUrl = apiURI + "/v1/browse/categories/" + categoryId + "/playlists";
        String answerFromServer = sendRequest(getNewUrl);
        if (answerFromServer.contains("error")) {
            return outputInfo;
        }

        JsonArray playlists = JsonParser.parseString(answerFromServer).getAsJsonObject().get("playlists").getAsJsonObject().get("items").getAsJsonArray();
        for (JsonElement currentPlaylist : playlists
        ) {
            String playlistName = currentPlaylist.getAsJsonObject().get("name").getAsString();
            String externalLinkUrl = currentPlaylist.getAsJsonObject().get("external_urls").getAsJsonObject().get("spotify").getAsString();
            outputInfo.add(playlistName + "\n" + externalLinkUrl);
        }
        return outputInfo;
    }

    private static String getCategoryId(String category) {
        String getNewUrl = apiURI + "/v1/browse/categories";
        String answerFromServer = sendRequest(getNewUrl);
        JsonArray categories = JsonParser.parseString(answerFromServer).getAsJsonObject().get("categories").getAsJsonObject().get("items").getAsJsonArray();
        for (JsonElement currentCategory : categories
        ) {
            if (currentCategory.getAsJsonObject().get("name").getAsString().contains(category)) {
                return currentCategory.getAsJsonObject().get("id").getAsString();
            }
        }
        return "unknown";
    }
}
