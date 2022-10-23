package advisor;

import java.util.Scanner;

public class Main {
    private static boolean isWorkingState = true;
    private static boolean isAuthorized = false;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (isWorkingState) {
            String inputStr = scanner.nextLine();
            String[] inputStrArgs = inputStr.split(" ");
            if (!isAuthorized && !inputStrArgs[0].equals("auth") && !inputStrArgs[0].equals("exit")) {
                System.out.println("Please, provide access for application.");
            } else {
                switch (inputStrArgs[0]) {
                    case "new" -> System.out.println("""
                            ---NEW RELEASES---
                            Mountains [Sia, Diplo, Labrinth]
                            Runaway [Lil Peep]
                            The Greatest Show [Panic! At The Disco]
                            All Out Life [Slipknot]""");
                    case "featured" -> System.out.println("""
                            ---FEATURED---
                            Mellow Morning
                            Wake Up and Smell the Coffee
                            Monday Motivation
                            Songs to Sing in the Shower""");
                    case "categories" -> System.out.println("""
                            ---CATEGORIES---
                            Top Lists
                            Pop
                            Mood
                            Latin""");
                    case "playlists" -> {
                        String band = inputStrArgs[1];
                        System.out.println("---" + band + """
                                 PLAYLISTS---
                                Walk Like A Badass
                                Rage Beats
                                Arab Mood Booster
                                Sunday Stroll""");
                    }
                    case "auth" -> {
                        System.out.println("https://accounts.spotify.com/authorize?client_id=41e5fb02a8a94cb9981e98df582be8d9&redirect_uri=http://localhost:8080&response_type=code");
                        System.out.println("---SUCCESS---");
                        isAuthorized = true;
                    }
                    case "exit" -> {
                        isWorkingState = false;
                        System.out.println("---GOODBYE!---");
                    }
                }
            }
        }
    }
}
