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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.utilities.SerpHandler;

public class HealthcareFragment extends Fragment {
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private YouTubePlayerView youTubePlayerView;

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

        // Set up YouTubePlayerView
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                // You can handle YouTubePlayer events here
            }
        });

        // Perform YouTube API operations when video links are available
        String searchQuery = "Healthcare";
        SerpHandler sh = new SerpHandler();
        CompletableFuture<JSONObject> future = sh.performSerpQuery(searchQuery);

        future.thenAccept(result -> {
            try {
                JSONArray videos = result.getJSONArray("video_results");
                if (videos.length() > 0) {
                    // Get the first video link
                    JSONObject video = videos.getJSONObject(0);
                    String videoLink = video.getString("link");

                    // Extract video ID from YouTube video link
                    String videoId = extractVideoId(videoLink);

                    // Load the video into YouTubePlayerView
                    youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> {
                        youTubePlayer.cueVideo(videoId, 0);
                    });
                } else {
                    // Handle case when no videos are found
                    Toast.makeText(requireContext(), "No videos found", Toast.LENGTH_SHORT).show();
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

    private String extractVideoId(String videoLink) {
        // Extract video ID from YouTube video link
        // Example link: https://www.youtube.com/watch?v=IB5qg81T8hg
        String[] parts = videoLink.split("=");
        if (parts.length > 1) {
            return parts[1];
        } else {
            return "";
        }
    }
}
