package it.uniba.dib.sms232417.asilapp.patientsFragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.osmdroid.views.MapView;
import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;


import com.amulyakhare.textdrawable.BuildConfig;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import it.uniba.dib.sms232417.asilapp.R;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private ManageJson manageJson = new ManageJson();
    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        TextView textView = view.findViewById(R.id.resultsTextView); // Sostituisci con l'ID del tuo TextView

        Map<String, String> parameters = new HashMap<>();
        parameters.put("engine", "google_maps");
        parameters.put("q", "pizza");
        parameters.put("ll", "@40.7455096,-74.0083012,15.1z");
        parameters.put("type", "search");
        parameters.put("api_key", "97f69511113f9b69bdba905e6f9c3315e9c6eec0e678136469e2c057d793cd9c");

        CompletableFuture<JSONObject> future = manageJson.performMapsQuery(parameters);

        future.thenAccept(response -> {
            try {
                // Parse the JSON response
                JSONArray localResults = response.getJSONArray("local_results");
                for (int i = 0; i < localResults.length(); i++) {
                    final int finalI = i; // Create a final variable
                    JSONObject localResult = localResults.getJSONObject(i);
                    JSONObject gpsCoordinates = localResult.getJSONObject("gps_coordinates");
                    double lat = gpsCoordinates.getDouble("latitude");
                    double lng = gpsCoordinates.getDouble("longitude");

                    // Create a LatLng
                    LatLng point = new LatLng(lat, lng);

                    // Add a marker on the map
                    getActivity().runOnUiThread(() -> {
                        mMap.addMarker(new MarkerOptions().position(point));

                        // Move the camera to the first marker
                        if (finalI == 0) { // Use the final variable here
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}