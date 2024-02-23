package it.uniba.dib.sms232417.asilapp.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.CompletableFuture;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SerpHandler {

    public CompletableFuture<JSONObject> performSerpQuery(String key) {
        return CompletableFuture.supplyAsync(() -> {
            //String api_key = "25605ae1d2f3ffe0a57320cd67406cabfcfb5f5b7a4ba4e4efc37c2e9bc0b27d"; Simone
            //String api_key = "3a6110547633c0044effcf96447f540b4c7cf859546c836c206e28d04f1710c8"; //Sergio
            String api_key = "";
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

    public String extractTitle(JSONObject videoResult) throws JSONException {
        return videoResult.getString("title");
    }

    public String extractLink(String videoLink) throws JSONException {
        // Extract video ID from YouTube video link
        // Example link: https://www.youtube.com/watch?v=IB5qg81T8hg
        String[] parts = videoLink.split("=");
        if (parts.length > 1) {
            return parts[1];
        } else {
            return "";
        }
    }

    public JSONObject extractChannel(JSONObject videoResult) throws JSONException {
        return videoResult.getJSONObject("channel");
    }

    public Date extractPublishedDate(JSONObject videoResult) throws JSONException {
        String publishedDateString = videoResult.getString("published_date");
        // Estrai il numero e l'unità di tempo da "X years ago" o "X hours ago"
        String[] parts = publishedDateString.split(" ");
        int number = Integer.parseInt(parts[0]);
        String unit = parts[1];

        // Crea un calendario per la data corrente
        Calendar calendar = Calendar.getInstance();

        // Sottrai il numero di anni o ore
        if (unit.startsWith("year")) {
            calendar.add(Calendar.YEAR, -number);
        } else if (unit.startsWith("hour")) {
            calendar.add(Calendar.HOUR, -number);
        }

    // Restituisci la data risultante
    return calendar.getTime();
}
    public int extractViews(JSONObject videoResult) throws JSONException {
        return videoResult.getInt("views");
    }

    public String extractLength(JSONObject videoResult) throws JSONException {
        return videoResult.getString("length");
    }

    public String extractDescription(JSONObject videoResult) throws JSONException {
        return videoResult.getString("description");
    }

    public JSONArray extractExtensions(JSONObject videoResult) throws JSONException {
        if (videoResult.has("extensions")) {
            return videoResult.getJSONArray("extensions");
        } else {
            return new JSONArray(); // Restituisci un array vuoto se non ci sono estensioni
        }
    }

    public String extractThumbnail(JSONObject videoResult) throws JSONException {
        if (videoResult.has("thumbnail")) {
            JSONObject thumbnailObject = videoResult.getJSONObject("thumbnail");
            if (thumbnailObject.has("rich")) {
                return thumbnailObject.getString("rich");
            } else if (thumbnailObject.has("static")) {
                return thumbnailObject.getString("static");
            }
        }
        return ""; // Restituisci una stringa vuota se non ci sono thumbnail
    }
}
