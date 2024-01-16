package it.uniba.dib.sms232417.asilapp.utilities;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class SerpHandler {

    public CompletableFuture<JSONObject> performSerpQuery(String key) {
        return CompletableFuture.supplyAsync(() -> {
            String api_key = "25605ae1d2f3ffe0a57320cd67406cabfcfb5f5b7a4ba4e4efc37c2e9bc0b27d";
            String url = "https://serpapi.com/search?engine=youtube&api_key=" + api_key + "&search_query=" + key;
            StringBuilder response = new StringBuilder();
            try {
                URL apiUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("GET");

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
                connection.disconnect();

                return new JSONObject(response.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new JSONObject();
        });
    }
}
