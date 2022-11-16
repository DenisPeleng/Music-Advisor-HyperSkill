package advisor.App;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static advisor.View.Viewer.printInfoInPages;


public class MusicAdviserApp {
    private static boolean isWorkingState = true;
    private static boolean isAuthorized = false;
    private static String accessToken = "";
    private static int itemsPerPage = 5;

    public static void start(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-access")) {
                Authorization.setAccessURI(args[i + 1]);
            }
            if (args[i].equals("-resource")) {
                SpotifyApiProcessing.setApiURI(args[i + 1]);
            }
            if (args[i].equals("-page")) {
                itemsPerPage = Integer.parseInt(args[i + 1]);
            }
        }
        List<String> arrayWithInfo = new ArrayList<>();
        int currentPage = 1;
        Scanner scanner = new Scanner(System.in);
        while (isWorkingState) {
            String inputStr = scanner.nextLine();
            String[] inputStrArgs = inputStr.split(" ");
            if (!isAuthorized && !inputStrArgs[0].equals("auth") && !inputStrArgs[0].equals("exit")) {
                System.out.println("Please, provide access for application.");
            } else {
                switch (inputStrArgs[0]) {
                    case "new" -> {
                        arrayWithInfo = SpotifyApiProcessing.getNew();
                        printInfoInPages(currentPage, itemsPerPage, arrayWithInfo);
                    }
                    case "featured" -> {
                        arrayWithInfo = SpotifyApiProcessing.getFeatured();
                        printInfoInPages(currentPage, itemsPerPage, arrayWithInfo);
                    }
                    case "categories" -> {
                        arrayWithInfo = SpotifyApiProcessing.getCategories();
                        printInfoInPages(currentPage, itemsPerPage, arrayWithInfo);
                    }
                    case "playlists" -> {
                        String category = inputStrArgs[1];
                        arrayWithInfo = SpotifyApiProcessing.getPlayLists(category);
                        if (!arrayWithInfo.isEmpty()) {
                            printInfoInPages(currentPage, itemsPerPage, arrayWithInfo);
                        }

                    }
                    case "auth" -> {
                        accessToken = Authorization.getAuthToken();
                        isAuthorized = true;
                    }
                    case "prev" -> {
                        currentPage--;
                        if (!printInfoInPages(currentPage, itemsPerPage, arrayWithInfo)) {
                            currentPage++;
                        }
                    }
                    case "next" -> {
                        currentPage++;
                        if (!printInfoInPages(currentPage, itemsPerPage, arrayWithInfo)) {
                            currentPage--;
                        }

                    }
                    case "exit" -> {
                        if (arrayWithInfo.isEmpty()) {
                            isWorkingState = false;
                        } else {
                            arrayWithInfo.clear();
                        }
                    }
                    default -> System.out.println("Unknown command");
                }
            }
        }
    }

    public static String getAccessToken() {
        return accessToken;
    }
}
