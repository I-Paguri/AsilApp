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
import it.uniba.dib.sms232417.asilapp.entity.Treatment;
import it.uniba.dib.sms232417.asilapp.interfaces.OnPatientDataCallback;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.interfaces.OnCountCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnTreatmentsCallback;

public class DatabaseAdapterPatient {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Patient patient;
    Patient resultPatient;
    Context context;

    public DatabaseAdapterPatient(Context context) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    public void onLogin(String emailIns, String password, OnPatientDataCallback callback) {

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(emailIns, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser utente = mAuth.getCurrentUser();
                        db = FirebaseFirestore.getInstance();
                        Log.d("LOGIN", "Login effettuato con successo");

                        db.collection("patient")
                                .document(utente.getUid())
                                .get()
                                .addOnSuccessListener(datiUtente -> {
                                    if (datiUtente.exists()) {
                                        resultPatient = new Patient(utente.getUid(), datiUtente.getString("nome"),
                                                datiUtente.getString("cognome"),
                                                datiUtente.getString("email"),
                                                datiUtente.getString("dataNascita"),
                                                datiUtente.getString("regione"));
                                        callback.onCallback(resultPatient);
                                    } else {
                                        callback.onCallbackError(new Exception(), context.getString(R.string.error_login_section_doctor));
                                    }
                                })
                                .addOnFailureListener(task1 -> {
                                    Log.d("Error", task1.toString());
                                    callback.onCallbackError(new Exception(), task1.toString());
                                });

                    }
                })
                .addOnFailureListener(task -> {
                    callback.onCallbackError(new Exception(), task.toString());

                });
    }

    public void onRegister(String nome, String cognome, String email, String dataNascita, String regione, String password, OnPatientDataCallback callback) {

        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser utente = mAuth.getCurrentUser();
                        db = FirebaseFirestore.getInstance();
                        Log.d("REGISTER", "Registrazione effettuata con successo");

                        patient = new Patient(utente.getUid(), nome, cognome, email, dataNascita, regione);

                        db.collection("patient")
                                .document(utente.getUid())
                                .set(patient)
                                .addOnSuccessListener(aVoid -> {
                                    callback.onCallback(patient);
                                });
                    }
                })
                .addOnFailureListener(task -> {
                    callback.onCallbackError(new Exception(), task.toString());
                });
    }

    public void onLogout() {
        mAuth.signOut();
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


    public void deleteTreatment(String patientUUID, String treatmentId) {
        db.collection("patient")
                .document(patientUUID)
                .collection("treatments")
                .document(treatmentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Treatment successfully deleted!");
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error deleting treatment", e);
                });
    }

    public void connectToContainer(String token, String patientUUID){

        db.collection("qr_code_container")
                .document(token)
                .update("uuid", patientUUID)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Container successfully connected!");
                })
                .addOnFailureListener(e -> {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                    builder.setTitle("Errore");
                    builder.setMessage(e.getMessage());
                    builder.setPositiveButton("OK", null);
                });


    }
}
