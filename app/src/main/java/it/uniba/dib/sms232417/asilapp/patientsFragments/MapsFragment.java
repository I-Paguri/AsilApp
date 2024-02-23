package it.uniba.dib.sms232417.asilapp.patientsFragments;

import static androidx.core.location.LocationManagerCompat.requestLocationUpdates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterUser;
import it.uniba.dib.sms232417.asilapp.adapters.ProductAdapter;
import it.uniba.dib.sms232417.asilapp.entity.AsylumHouse;
import it.uniba.dib.sms232417.asilapp.entity.Medication;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;
import it.uniba.dib.sms232417.asilapp.interfaces.OnAsylumHouseDataCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnAsylumHouseRatingCallback;
import it.uniba.dib.sms232417.asilapp.utilities.review.MaterialRating;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import android.Manifest;
import android.widget.Toast;
//import it.uniba.dib.sms232417.asilapp.Manifest;
import it.uniba.dib.sms232417.asilapp.R;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private ManageJson manageJson = new ManageJson();
    private GoogleMap mMap;

    private List<AsylumHouse> asylumHouseList = new ArrayList<>();

    private String markerTitle;

    // HashMap per memorizzare i dati della risposta JSON
    Map<String, Map<String, String>> markerData = new HashMap<>();


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

        // Ottieni i riferimenti ai TextView
        TextView titleTextView = getView().findViewById(R.id.titleLocation);
        TextView addressTextView = getView().findViewById(R.id.addressLocation);
        TextView descriptionTextView = getView().findViewById(R.id.descriptionLocation);
        TextView ratingTextView = getView().findViewById(R.id.rating);
        ImageView ratingImage = getView().findViewById(R.id.ratingStar);

        titleTextView.setVisibility(View.GONE);
        addressTextView.setVisibility(View.GONE);
        descriptionTextView.setVisibility(View.GONE);
        ratingTextView.setVisibility(View.GONE);
        ratingImage.setVisibility(View.GONE);


        // Set the title of the fragment richiamando il metodo
        setTitle();
        //inizializzo button
        Button recensioneButton = view.findViewById(R.id.recensioneButton);

        FloatingActionButton my_location_button = view.findViewById(R.id.my_location_button);


        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.Services_list);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.searchOnMaps_array, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setAdapter(adapter);


        //valuto la rimozione del bottone

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


        FloatingActionButton location = view.findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // If the permissions are not granted, request them
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                } else {

                    // If the permissions are granted, call the API to get the location
                    String query = autoCompleteTextView.getText().toString();


                    if (query.isEmpty()) {
                        // Show a Toast message
                        Toast.makeText(getContext(), getResources().getString(R.string.select_nearby), Toast.LENGTH_SHORT).show();
                    } else {
                        if (!query.equals("Centri Asilo") && !query.equals("Asylum House")) {
                            mMap.clear();
                            titleTextView.setVisibility(View.GONE);
                            addressTextView.setVisibility(View.GONE);
                            descriptionTextView.setVisibility(View.GONE);
                            ratingTextView.setVisibility(View.GONE);
                            ratingImage.setVisibility(View.GONE);

                            recensioneButton.setVisibility(View.GONE);

                            callApi(query);

                            resultLocationApi(mMap, titleTextView, addressTextView);

                            //metodo per verficare se è cliccato un marker o la mappa
                            MarkerClickResult(mMap, titleTextView, addressTextView);

                        } else {
                            mMap.clear();
                            titleTextView.setVisibility(View.GONE);
                            addressTextView.setVisibility(View.GONE);
                            descriptionTextView.setVisibility(View.GONE);
                            ratingTextView.setVisibility(View.GONE);
                            ratingImage.setVisibility(View.GONE);

                            // Aggiungi un marker a una posizione specifica
                            addMarker(mMap);


                            mMap.setOnMarkerClickListener(marker -> {
                                // imposto regolamento e bottone visibile

                                markerTitle = marker.getTitle();
                                resultLocationCentriAsilo(mMap, titleTextView, descriptionTextView, addressTextView, markerTitle, ratingTextView, ratingImage);
                                recensioneButton.setVisibility(View.VISIBLE);

                                return false;
                            });

                            //metodo per verficare se è cliccato un marker o la mappa
                            MarkerClick(mMap, recensioneButton, titleTextView, addressTextView, descriptionTextView, ratingTextView, ratingImage);

                            //listener per il bottone recensione
                            clickRecensioneButton(recensioneButton, ratingTextView);


                        }

                    }
                }
            }
        });

        //chiedo i permessi appena si apre il fragment
        PermessiRichiesti();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        ImageButton infoButton = view.findViewById(R.id.info_button);

        //verifico se è cliccato il bottone per le informazioni
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Informazioni")
                        .setMessage("Se rifiuti 2 volte i permessi non sarà possibile mostrarti i punti di interesse." +
                                "\n\nSe non vedi i punti di interesse, controlla di aver attivato la localizzazione sul tuo dispositivo." +
                                "\n\nPuoi recensire i centri anche senza concedere i permessi.")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //nascondo il pulsante per la posizione corrente predefinito di google
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

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
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

        // Crea un ProgressDialog
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading..."); // Imposta il messaggio
        progressDialog.setCancelable(false); // Imposta il ProgressDialog non cancellabile

        // Mostra il ProgressDialog prima di iniziare la chiamata API
        progressDialog.show();

        Map<String, String> parameters = new HashMap<>();
        //modofico in modo da non consumare troppe richieste
        //parameters.put("engine", "google_maps");
        parameters.put("engine", "google_maps");
        parameters.put("q", query);
        parameters.put("ll", "@" + latitude + "," + longitude + ",14z");
        parameters.put("type", "search");
        parameters.put("api_key", "97f69511113f9b69bdba905e6f9c3315e9c6eec0e678136469e2c057d793cd9c");
        parameters.put("hl", "it");

        CompletableFuture<JSONObject> future = manageJson.performMapsQuery(parameters);

        future.thenAccept(response -> {
            // Chiudi il ProgressDialog dopo aver ricevuto la risposta
            progressDialog.dismiss();

            try {

                // Parse the JSON response
                JSONArray localResults = response.getJSONArray("local_results");
                //stampo localResults con log
                Log.d("localResults", localResults.toString());
                for (int i = 0; i < localResults.length(); i++) {
                    final int finalI = i; // Create a final variable
                    JSONObject localResult = localResults.getJSONObject(i);
                    JSONObject gpsCoordinates = localResult.getJSONObject("gps_coordinates");
                    double lat = gpsCoordinates.getDouble("latitude");
                    double lng = gpsCoordinates.getDouble("longitude");

                    // Create a LatLng
                    LatLng point = new LatLng(lat, lng);

                    String title = localResult.getString("title");
                    Log.d("title", title);

                    String address = localResult.getString("address");


                    // Crea un HashMap per i dati del marker
                    Map<String, String> data = new HashMap<>();
                    data.put("title", title);
                    data.put("address", address);

                    markerData.put(title, data);


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
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.nearby_facilities));

    }


    public void PermessiRichiesti() {
        // Richiesta dei permessi non appena si apre il fragment
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 20);
        }
    }

    //metodo per verificare se è cliccato un marker o la mappa nel caso dei Centri asilo
    public void MarkerClick(GoogleMap map, Button recensioneButton, TextView titleTextView, TextView addressTextView, TextView descriptionTextView, TextView ratingTextView, ImageView ratingImage) {
        mMap.setOnMapClickListener(latLng -> {
            recensioneButton.setVisibility(View.GONE);
            titleTextView.setVisibility(View.GONE);
            addressTextView.setVisibility(View.GONE);
            descriptionTextView.setVisibility(View.GONE);
            ratingTextView.setVisibility(View.GONE);
            ratingImage.setVisibility(View.GONE);


        });
    }

    //metodo per verificare se è cliccato un marker o la mappa nel caso di ricerca API
    public void MarkerClickResult(GoogleMap mMap, TextView titleTextView, TextView addressTextView) {
        mMap.setOnMapClickListener(latLng -> {
            titleTextView.setVisibility(View.GONE);
            addressTextView.setVisibility(View.GONE);
        });
    }

    //metodo per aggiungere i marker dei centri predefiniti sulla mappa
    public void addMarker(GoogleMap mMap) {

        //connessione db e recupero dati per aggiungere i marker

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            DatabaseAdapterUser adapter = new DatabaseAdapterUser(getContext());
            adapter.getAsylumHouses(new OnAsylumHouseDataCallback() {
                @Override
                public void onCallback(List<AsylumHouse> asylumHouse) {

                    asylumHouseList = asylumHouse;

                    for (AsylumHouse house : asylumHouse) {
                        LatLng posizione = new LatLng(house.getCoordinates().getLatitude(), house.getCoordinates().getLongitude());
                        mMap.addMarker(new MarkerOptions().position(posizione).title(house.getName()));
                    }
                }


                @Override
                public void onCallbackFailed(Exception e) {

                }


            });
        }
    }


    //metodo per salvare la risposta della recensione

    public void resultLocationApi(GoogleMap mMap, TextView titleTextView, TextView addressTextView) {

        mMap.setOnMarkerClickListener(marker -> {
            // Ottieni i dati dal marker
            String title = marker.getTitle();
            Map<String, String> data = markerData.get(title);


            // Imposta il testo dei TextView
            titleTextView.setText(data.get("title"));
            addressTextView.setText(data.get("address"));

            titleTextView.setVisibility(View.VISIBLE);
            addressTextView.setVisibility(View.VISIBLE);

            // Imposta qui il testo degli altri TextView...
            // Ritorna false per indicare che non abbiamo consumato l'evento,
            // quindi l'azione di default (mostrare l'info window) sarà eseguita.
            return false;
        });
    }

    public void resultLocationCentriAsilo(GoogleMap mMap, TextView titleTextView, TextView descriptionTextView, TextView addressTextView, String markerTitle, TextView ratingTextView, ImageView ratingImage) {

        for (AsylumHouse house : asylumHouseList) {
            if (house.getName().equals(markerTitle)) {

                titleTextView.setText(house.getName());
                float rating = (float) house.getRatingAverage();
                String formattedRating = String.format("%.2f", rating);
                ratingTextView.setText(formattedRating);
                house.getRules().forEach(rule -> {
                    descriptionTextView.append(rule + "\n\n");
                });

                    addressTextView.setText(house.getAddress());

                    titleTextView.setVisibility(View.VISIBLE);
                    addressTextView.setVisibility(View.VISIBLE);
                    descriptionTextView.setVisibility(View.VISIBLE);
                    ratingTextView.setVisibility(View.VISIBLE);
                    ratingImage.setVisibility(View.VISIBLE);
            }

                }
            }

    public void clickRecensioneButton(Button recensioneButton, TextView ratingTextView) {
        //imposto un listener per gli eventi sul button
        recensioneButton.setOnClickListener(view1 -> {
            // Mostra il pop-up
            FragmentManager fragmentManager = getParentFragmentManager();
            MaterialRating feedBackDialog = new MaterialRating();
            feedBackDialog.show(fragmentManager, "rating");

            feedBackDialog.setOnRatingSentListener(rating -> {

                // Gestisci il rating qui
                Log.d("MapsFragment", "Rating ricevuto: " + rating);
                Log.d("MapsFragment", "Centro Asilo: " + markerTitle);

         //salvo il rating nel db
        DatabaseAdapterUser dbAdapterUser = new DatabaseAdapterUser(requireContext());

        // Qui devi assegnare il nome del centro asilo ottenuto dal Marker
        String name = markerTitle;
        // Ottengo tutti i centri asilo
        dbAdapterUser.getAsylumHouses(new OnAsylumHouseDataCallback() {
            @Override
            public void onCallback(List<AsylumHouse> asylumHouses) {
                int i;
                boolean asylumHouseFound;
                AsylumHouse asylumHouse;

                i = 0;
                asylumHouseFound = false;
                asylumHouse = null;
                // Cerco il centro asilo con il nome ottenuto dal Marker
                while (asylumHouseFound == false && i < asylumHouses.size()) {
                    asylumHouse = asylumHouses.get(i);

                    if (asylumHouse.getName().equals(name)) {
                        asylumHouseFound = true;
                    }
                    i++;
                }

                if (asylumHouseFound) {
                    // Se ho trovato un centro asilo con il nome ottenuto dal Marker, ricavo l'UUID
                    String asylumHouseUUID = asylumHouse.getUUID();

                    // Aggiungo il rating al centro asilo con l'UUID ottenuto
                    dbAdapterUser.addRating(asylumHouseUUID, rating, new OnAsylumHouseRatingCallback() {
                        @Override
                        public void onCallback(double rating) {
                            Log.d("Rating", "New rating: " + rating);
                            Toast.makeText(getContext(), getResources().getString(R.string.you_reviewed)+markerTitle, Toast.LENGTH_SHORT).show();
                            addMarker(mMap);
                            updateTextViewRating(ratingTextView, markerTitle);

                        }
                        @Override
                        public void onCallbackFailed(Exception e) {
                            Log.d("Rating", "Error adding rating", e);
                        }
                    });
                } else {
                    Log.d("AsylumHouse", "Asylum house not found");
                }
            }

            @Override
            public void onCallbackFailed(Exception e) {
                Log.d("AsylumHouse", "Error getting asylum houses", e);
            }
        });

            });
        });
    }

    public void updateTextViewRating(TextView ratingTextView, String markerTitle) {

        //prendo valore da db
        DatabaseAdapterUser dbAdapterUser = new DatabaseAdapterUser(requireContext());

        // Ottengo tutti i centri asilo
        dbAdapterUser.getAsylumHouses(new OnAsylumHouseDataCallback() {
            @Override
            public void onCallback(List<AsylumHouse> asylumHouses) {

               for (AsylumHouse house : asylumHouses) {

                    if (house.getName().equals(markerTitle)) {

                        float rating = (float) house.getRatingAverage();
                        String formattedRating = String.format("%.2f", rating);
                        ratingTextView.setText(formattedRating);
                    }

                }
            }

            @Override
            public void onCallbackFailed(Exception e) {
                Log.d("AsylumHouse", "Error getting asylum houses", e);
            }
        });


    }



}
