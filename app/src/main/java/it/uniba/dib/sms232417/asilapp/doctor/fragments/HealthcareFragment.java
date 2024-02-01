package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.utilities.SerpHandler;
import it.uniba.dib.sms232417.asilapp.utilities.Video;

public class HealthcareFragment extends Fragment {
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private YouTubePlayerView youTubePlayerView;

    // Lista per memorizzare gli ID dei video
    private List<String> videoIds = new ArrayList<>();

    // Indice per tenere traccia del video corrente
    private int currentVideoIndex = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_healthcare, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        bottomNavigationView = requireActivity().findViewById(R.id.nav_view);
        toolbar = requireActivity().findViewById(R.id.toolbar);
        youTubePlayerView = view.findViewById(R.id.youtubePlayerView);

        // Perform YouTube API operations when video links are available
        String searchQuery = "Healthcare";
        SerpHandler sh = new SerpHandler();
        CompletableFuture<JSONObject> future = sh.performSerpQuery(searchQuery);


        // Set up YouTubePlayerView
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                future.thenAccept(result -> {
                    try {
                        JSONArray videos = result.getJSONArray("video_results");
                        if (videos.length() > 0) {
                            // Get the first video link
                            JSONObject video = videos.getJSONObject(0);
                            String videoLink = video.getString("link");

                            // Extract video ID from YouTube video link using SerpHandler
                            SerpHandler sh = new SerpHandler();
                            String videoId = sh.extractLink(videoLink);

                            // Load the video into YouTubePlayerView
                            youTubePlayer.cueVideo(videoId, 0);

                            // Store video IDs for later use
                            for (int i = 0; i < videos.length(); i++) {
                                video = videos.getJSONObject(i);
                                videoLink = video.getString("link");
                                videoId = sh.extractLink(videoLink);
                                videoIds.add(videoId);
                            }
                        } else {
                            // Handle case when no videos are found
                            Toast.makeText(requireContext(), "No videos found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                if (state == PlayerConstants.PlayerState.ENDED) {
                    // Play next video when current video ends
                    currentVideoIndex = (currentVideoIndex + 1) % videoIds.size();
                    youTubePlayer.loadVideo(videoIds.get(currentVideoIndex), 0);
                }
            }
        });

        future.thenAccept(result -> {
            try {
                if (result.has("video_results")) {
                    JSONArray videoResults = result.getJSONArray("video_results");

                    // Estraggo i dati dal JSON e creo un oggetto Video per ogni risultato
                    for (int i = 0; i < videoResults.length(); i++) {
                        JSONObject currentObject = videoResults.getJSONObject(i);

                        String title = sh.extractTitle(currentObject);
                        String link = sh.extractLink(currentObject.getString("link"));
                        String channel = sh.extractChannel(currentObject).getString("name");
                        Date publishedDate = sh.extractPublishedDate(currentObject);
                        int views = sh.extractViews(currentObject);
                        String length = sh.extractLength(currentObject);
                        String description = sh.extractDescription(currentObject);
                        JSONArray extensions = sh.extractExtensions(currentObject);
                        String thumbnail = sh.extractThumbnail(currentObject);

                        Video video = new Video(title, link, channel, publishedDate, views, length, description, extensions, thumbnail);

                        System.out.println("Video " + i + ":");
                        System.out.println("Title: " + video.getTitle());
                        System.out.println("Link: " + video.getLink());
                        System.out.println("Channel: " + video.getChannel());
                        System.out.println("Published Date: " + video.getPublishedDate());
                        System.out.println("Views: " + video.getViews());
                        System.out.println("Length: " + length);
                        System.out.println("Description: " + description);
                        System.out.println("Extensions: " + extensions);
                        System.out.println("Thumbnail: " + thumbnail);
                    }

                } else {
                    System.out.println("Oggetto 'video_results' non trovato in 'result'.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        // Set up the toolbar
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Show home button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set home icon as back button
        Drawable homeIcon = getResources().getDrawable(R.drawable.home, null);
        // Set color filter
        homeIcon.setColorFilter(getResources().getColor(R.color.md_theme_light_surface), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeAsUpIndicator(homeIcon);

        // Set toolbar title
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.healthcare));
        // Change toolbar title text color
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_theme_light_surface));

        // Set navigation click listener
        toolbar.setNavigationOnClickListener(v -> {
            // Navigate to HomeFragment
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, new HomeFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}
