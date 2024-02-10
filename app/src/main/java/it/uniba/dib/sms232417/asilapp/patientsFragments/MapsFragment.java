package it.uniba.dib.sms232417.asilapp.patientsFragments;

import static androidx.core.location.LocationManagerCompat.requestLocationUpdates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import android.Manifest;
//import it.uniba.dib.sms232417.asilapp.Manifest;
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

        // Set the title of the fragment richiamando il metodo
        setTitle();

        FloatingActionButton my_location_button = view.findViewById(R.id.my_location_button);

        //popolo spinner
        Spinner spinner = view.findViewById(R.id.spinner);
        // Crea un ArrayAdapter utilizzando l'array di stringhe e il layout predefinito dello spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.searchOnMaps_array, android.R.layout.simple_spinner_item);        // Specifica il layout da utilizzare quando appare la lista di scelte
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Applica l'adapter allo spinner
        spinner.setAdapter(adapter);



        FloatingActionButton fab = view.findViewById(R.id.fab);
       //per ora non serve, prossimo alla rimozione
        fab.setVisibility(View.GONE);
        my_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLocationUpdates();
                enableMyLocation();
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastKnownLocation != null) {
                        LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                    }
                }
            }
        });



        // Richiesta dei permessi non appena si apre il fragment
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }



        FloatingActionButton medicalCrossButton = view.findViewById(R.id.medical_cross_button);
        medicalCrossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // If the permissions are granted, call the API
                    String query = spinner.getSelectedItem().toString();
                    callApi(query);
                }
            }
        });



        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        ImageButton infoButton = view.findViewById(R.id.info_button);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Informazioni")
                        .setMessage("Se rifiuti 2 volte i permessi non sarà possibile mostrarti i punti di interesse.")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //nascondo il pulsante per la posizione corrente
        if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
            //disabilito i pulsanti per la navigazione prefefiniti di google
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(false);
        }
    }




    private void requestLocationUpdates() {
        // Check if the location permissions are granted
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the location permissions
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        } else {

            // The location permissions are granted, start getting location updates
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    float latitude = (float) location.getLatitude();
                    float longitude = (float) location.getLongitude();

                    // Save the location in the SharedPreferences
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("latitude", latitude);
                    editor.putFloat("longitude", longitude);
                    editor.apply();

                    Log.d("MapsFragment", "Saved Latitude: " + latitude);
                    Log.d("MapsFragment", "Saved Longitude: " + longitude);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };

            // Register the listener with the Location Manager to receive location updates
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    //metodo che serve a richiamare il metodo in ManageJson per gestire la risposta json
    private void callApi(String query) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        float latitude = sharedPreferences.getFloat("latitude", 0);
        float longitude = sharedPreferences.getFloat("longitude", 0);


        Map<String, String> parameters = new HashMap<>();
        //modofico in modo da non consumare troppe richieste
        //parameters.put("engine", "google_maps");
        parameters.put("engine", "google_maps");
        parameters.put("q", query);
        parameters.put("ll", "@" + latitude + "," + longitude + ",14z");
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

                    String title = localResult.getString("title");

                    // Add a marker on the map
                    getActivity().runOnUiThread(() -> {
                        mMap.addMarker(new MarkerOptions().position(point).title(title));

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

    //Il metodo enableMyLocation() nel tuo file MapsFragment.java è
    // utilizzato per abilitare
    // la visualizzazione della posizione corrente dell'utente
    // sulla mappa di Google Maps.
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }


    //metodo per impostare title
    public void setTitle() {

        // Show home button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable homeIcon = getResources().getDrawable(R.drawable.home, null);
        // Set color filter
        homeIcon.setColorFilter(getResources().getColor(R.color.md_theme_light_surface), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeAsUpIndicator(homeIcon);

        // Set toolbar title
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Servizi Vicini");

    }


}