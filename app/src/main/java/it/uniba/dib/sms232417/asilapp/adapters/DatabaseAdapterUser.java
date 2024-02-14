package it.uniba.dib.sms232417.asilapp.adapters;

import android.content.Context;
import android.util.Log;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;
import it.uniba.dib.sms232417.asilapp.interfaces.OnPatientDataCallback;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.interfaces.OnCountCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnProfileImageCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnTreatmentsCallback;

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

    private void getTreatmentCount(String patientUUID, OnCountCallback callback) {
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
                    callback.onCallback(treatments); // Pass the map to the callback
                })
                .addOnFailureListener(e -> {
                    callback.onCallbackFailed(e);
                });
    }
}
