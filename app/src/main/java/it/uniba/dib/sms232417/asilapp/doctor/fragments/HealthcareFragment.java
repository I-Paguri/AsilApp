package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.ImageAdapter;
import it.uniba.dib.sms232417.asilapp.utilities.SerpHandler;

public class HealthcareFragment extends Fragment {
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

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
        RecyclerView healthcareRecyclerView = view.findViewById(R.id.healthcareRecyclerView);
        healthcareRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        String healthcareQuery = getResources().getString(R.string.healthcare);
        setupCategory(healthcareQuery, healthcareRecyclerView);

        // Set up for mental health tips category
        RecyclerView mentalHealthRecyclerView = view.findViewById(R.id.mentalHealthRecyclerView);
        mentalHealthRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        String mentalHealthQuery = getResources().getString(R.string.mental_health_tips);
        setupCategory(mentalHealthQuery, mentalHealthRecyclerView);

        /*
        // Set up for healthy food tips category
        RecyclerView healthyFoodRecyclerView = view.findViewById(R.id.healthyFoodRecyclerView);
        healthyFoodRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        String healthyFoodQuery = getResources().getString(R.string.healthy_food_tips);
        setupCategory(healthyFoodQuery, healthyFoodRecyclerView);
        */

        ArrayList<SlideModel> imageList = new ArrayList<>(); // Create image list

// imageList.add(new SlideModel("String Url" or R.drawable));
// imageList.add(new SlideModel("String Url" or R.drawable, "title")); // You can add title

        imageList.add(new SlideModel("https://bit.ly/2YoJ77H", "The animal population decreased by 58 percent in 42 years."));
        imageList.add(new SlideModel("https://bit.ly/2BteuF2", "Elephants and tigers may become extinct."));
        imageList.add(new SlideModel("https://bit.ly/3fLJf72", "And people do that."));

        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        imageSlider.setImageList(imageList);

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

    private void setupCategory(String query, RecyclerView recyclerView) {
        List<String> thumbnailUrls = new ArrayList<>();
        List<String> videoUrls = new ArrayList<>(); // List of video URLs

        SerpHandler sh = new SerpHandler();
        CompletableFuture<JSONObject> future = sh.performSerpQuery(query);

        future.thenAccept(result -> {
            try {
                if (result.has("video_results")) {
                    JSONArray videoResults = result.getJSONArray("video_results");

                    for (int i = 0; i < videoResults.length(); i++) {
                        JSONObject currentObject = videoResults.getJSONObject(i);
                        String thumbnail = sh.extractThumbnail(currentObject);
                        String videoLink = sh.extractLink(currentObject.getString("link"));
                        String videoUrl = "https://www.youtube.com/watch?v=" + videoLink;

                        thumbnailUrls.add(thumbnail);
                        videoUrls.add(videoUrl); // Add video URL to list
                    }
                    if (!thumbnailUrls.isEmpty() && !videoUrls.isEmpty()) {
                        Context context = getContext();
                        if (context != null) {
                            requireActivity().runOnUiThread(() -> {
                                try {
                                    ImageAdapter imageAdapter = new ImageAdapter(context, thumbnailUrls, videoUrls); // Pass video URLs to ImageAdapter
                                    recyclerView.setAdapter(imageAdapter);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
}