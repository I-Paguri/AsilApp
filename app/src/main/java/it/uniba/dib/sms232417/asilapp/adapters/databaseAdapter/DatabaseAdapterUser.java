package it.uniba.dib.sms232417.asilapp.adapters.databaseAdapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms232417.asilapp.entity.AsylumHouse;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;
import it.uniba.dib.sms232417.asilapp.interfaces.OnAsylumHouseDataCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnAsylumHouseRatingCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnGetValueFromDBInterface;
import it.uniba.dib.sms232417.asilapp.interfaces.OnCountCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnTreatmentsCallback;
import it.uniba.dib.sms232417.asilapp.entity.vitals.BloodPressure;
import it.uniba.dib.sms232417.asilapp.entity.vitals.Glycemia;
import it.uniba.dib.sms232417.asilapp.entity.vitals.HeartRate;
import it.uniba.dib.sms232417.asilapp.entity.vitals.Temperature;

public class DatabaseAdapterUser {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Doctor doctor;
    Context context;

    public DatabaseAdapterUser(Context context) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    public void addTreatment(String patientUUID, Treatment treatment, OnTreatmentsCallback onTreatmentsCallback) {
        Log.d("AddedNewTreatment", treatment.toString());

        getTreatmentCount(patientUUID, new OnCountCallback() {
            @Override
            public void onCallback(int count) {
                String treatmentId = "treatment" + (count + 1);
                db.collection("patient")
                        .document(patientUUID)
                        .collection("treatments")
                        .document(treatmentId)
                        .set(treatment)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firestore", "Treatment successfully written!");
                            onTreatmentsCallback.onCallback(null);
                        })
                        .addOnFailureListener(e -> {
                            Log.w("Firestore", "Error writing treatment", e);
                            onTreatmentsCallback.onCallbackFailed(e);
                        });
            }

            @Override
            public void onCallbackFailed(Exception e) {
                Log.w("Firestore", "Error getting treatment count", e);
                onTreatmentsCallback.onCallbackFailed(e);
            }
        });
    }

    public void getTreatmentCount(String patientUUID, OnCountCallback callback) {
        try {
            db.collection("patient")
                    .document(patientUUID)
                    .collection("treatments")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        callback.onCallback(queryDocumentSnapshots.size());
                    })
                    .addOnFailureListener(e -> {
                        callback.onCallbackFailed(e);
                    });
        } catch (Exception e) {
            callback.onCallbackFailed(e);
        }
    }

    public void getTreatments(String patientUUID, OnTreatmentsCallback callback) {
        Log.d("Firestore", "Getting treatments");
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            db.collection("patient")
                    .document(patientUUID)
                    .collection("treatments")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        Map<String, Treatment> treatments = new HashMap<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Treatment treatment = doc.toObject(Treatment.class);
                            String treatmentId = doc.getId(); // Get the treatmentId

                            treatments.put(treatmentId, treatment); // Add the treatmentId and Treatment object to the map
                        }

                        // Convert the Map to a List of entries
                        List<Map.Entry<String, Treatment>> list = new ArrayList<>(treatments.entrySet());

                        // Sort the List using Collections.sort() and a custom comparator
                        Collections.sort(list, new Comparator<Map.Entry<String, Treatment>>() {
                            @Override
                            public int compare(Map.Entry<String, Treatment> o1, Map.Entry<String, Treatment> o2) {
                                return o2.getValue().getStartDate().compareTo(o1.getValue().getStartDate());
                            }
                        });

                        // Convert the sorted List back to a LinkedHashMap
                        Map<String, Treatment> sortedTreatments = new LinkedHashMap<>();
                        for (Map.Entry<String, Treatment> entry : list) {
                            sortedTreatments.put(entry.getKey(), entry.getValue());
                        }

                        Log.d("Firestore", "Treatments: " + treatments.toString());
                        callback.onCallback(sortedTreatments); // Pass the map to the callback
                    })
                    .addOnFailureListener(e -> {
                        Log.d("Firestore", "Error getting treatments", e);
                        callback.onCallbackFailed(e);
                    });
        } else {
            Log.d("Firestore", "No internet connection");
            callback.onCallbackFailed(new Exception("No internet connection"));
        }
    }

    public void getAsylumHouses(OnAsylumHouseDataCallback callback) {
        db.collection("asylumHouse")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<AsylumHouse> asylumHouses = queryDocumentSnapshots.toObjects(AsylumHouse.class);

                        callback.onCallback(asylumHouses);
                    } else {
                        Log.d("ASYLUMHOUSE", "Error");
                        callback.onCallbackFailed(new Exception("Error getting asylum houses"));
                    }
                });
    }

    public void addRating(String asylumHouseUUID, double rating, OnAsylumHouseRatingCallback callback) {
        db.collection("asylumHouse")
                .document(asylumHouseUUID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    AsylumHouse asylumHouse = documentSnapshot.toObject(AsylumHouse.class);
                    if (asylumHouse != null) {
                        double ratingAverage = asylumHouse.getRatingAverage();
                        int numberOfReviews = asylumHouse.getNumberOfReviews();

                        double newRatingAverage = (ratingAverage * numberOfReviews + rating) / (numberOfReviews + 1);
                        int newNumberOfReviews = numberOfReviews + 1;

                        Map<String, Object> data = new HashMap<>();
                        data.put("ratingAverage", newRatingAverage);
                        data.put("numberOfReviews", newNumberOfReviews);

                        db.collection("asylumHouse")
                                .document(asylumHouseUUID)
                                .update(data)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Rating successfully written!");
                                    callback.onCallback(newRatingAverage);
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("Firestore", "Error writing rating", e);
                                    callback.onCallbackFailed(e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error getting asylum house", e);
                    callback.onCallbackFailed(e);
                });
    }


    public void getVitals(String patientUUID, String measureType, OnGetValueFromDBInterface callback) {
        db = FirebaseFirestore.getInstance();

        db.collection("patient")
                .document(patientUUID)
                .collection(measureType)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Object> vitals = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (measureType.equals("heart_rate")) {
                                HeartRate hr = document.toObject(HeartRate.class);
                                vitals.add(hr);
                            } else if (measureType.equals("temperature")) {
                                Temperature t = document.toObject(Temperature.class);
                                vitals.add(t);
                            } else if (measureType.equals("blood_pressure")) {
                                BloodPressure bp = document.toObject(BloodPressure.class);
                                vitals.add(bp);
                            } else if (measureType.equals("glycemia")) {
                                Glycemia g = document.toObject(Glycemia.class);
                                vitals.add(g);
                            }
                        }
                        callback.onCallback(vitals);
                    } else {
                        callback.onCallbackError(new Exception("No data found"), "No data found");
                    }
                });
    }
}
