package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.utilities.SerpHandler;

public class HealthcareFragment extends Fragment {
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private List<String> videoUrlsHealthyFood; // List of video URLs for healthy food tips

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

        // Set up for healthcare category
        ImageSlider healthcareImageSlider = view.findViewById(R.id.healthcareImageSlider);
        String healthcareQuery = getResources().getString(R.string.healthcare);
        setupSlide(healthcareQuery, healthcareImageSlider);

        // Set up for mental health tips category
        ImageSlider mentalHealthcareimageSlider = view.findViewById(R.id.mentalHealthImageSlider);
        String mentalHealthQuery = getResources().getString(R.string.mental_health_tips);
        setupSlide(mentalHealthQuery, mentalHealthcareimageSlider);

        // Set up for healthy food tips category
        ImageSlider healthyFoodImageSlider = view.findViewById(R.id.healthyFoodImageSlider);
        String healthyFoodQuery = getResources().getString(R.string.healthy_food_tips);
        setupSlide(healthyFoodQuery, healthyFoodImageSlider);
        healthyFoodImageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void doubleClick(int i) {
                Toast.makeText(getContext(), "Double clicked image at position: " + i, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemSelected(int position) {
                if (!videoUrlsHealthyFood.isEmpty()) {
                    String videoUrl = videoUrlsHealthyFood.get(position); // Get the video URL
                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoUrl));

                    // Check if the YouTube app is installed
                    if (appIntent.resolveActivity(getContext().getPackageManager()) != null) {
                        // If YouTube app is installed, use it to open the video
                        getContext().startActivity(appIntent);
                    } else {
                        // If YouTube app is not installed, use the web browser to open the video
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                        getContext().startActivity(webIntent);
                    }
                } else {
                    Toast.makeText(getContext(), "No video URLs available", Toast.LENGTH_SHORT).show();
                }

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

    private void setupSlide(String query, ImageSlider imageSlider) {
        List<SlideModel> slideModels = new ArrayList<>();
        List<String> videoUrls = new ArrayList<>();

        SerpHandler sh = new SerpHandler();
        CompletableFuture<JSONObject> future = sh.performSerpQuery(query);

        future.thenAccept(result -> {
            try {
                if (result.has("video_results")) {
                    JSONArray videoResults = result.getJSONArray("video_results");

                    for (int i = 0; i < videoResults.length(); i++) {
                        JSONObject currentObject = videoResults.getJSONObject(i);
                        String thumbnail = sh.extractThumbnail(currentObject);
                        String videoUrl = sh.extractLink(currentObject.getString("link"));

                        slideModels.add(new SlideModel(thumbnail, "", ScaleTypes.FIT));
                        videoUrls.add(videoUrl);
                    }

                    if (!slideModels.isEmpty()) {
                        requireActivity().runOnUiThread(() -> {
                            imageSlider.setImageList(slideModels);
                            imageSlider.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void doubleClick(int i) {
                                    Toast.makeText(getContext(), "Double clicked image at position: " + i, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onItemSelected(int position) {
                                    String videoUrl = videoUrls.get(position);
                                    Log.i("HealthcareFragment", "Video URL: " + videoUrl);
                                    // Use the full YouTube URL when constructing the appIntent
                                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoUrl));

                                    if (appIntent.resolveActivity(getContext().getPackageManager()) != null) {
                                        getContext().startActivity(appIntent);
                                    } else {
                                        // If YouTube app is not installed, use the web browser to open the video
                                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoUrl));
                                        getContext().startActivity(webIntent);
                                    }
                                }
                            });
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

}