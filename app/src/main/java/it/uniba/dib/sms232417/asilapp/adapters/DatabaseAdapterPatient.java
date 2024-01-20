package it.uniba.dib.sms232417.asilapp.adapters;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;
import it.uniba.dib.sms232417.asilapp.entity.interface_entity.OnPatientDataCallback;
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
                                        resultPatient = new Patient(datiUtente.getString("nome"),
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

                        patient = new Patient(nome, cognome, email, dataNascita, regione);

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

    public void addTreatment(Treatment treatment) {
        getTreatmentCount(new OnCountCallback() {
            @Override
            public void onCallback(int count) {
                String treatmentId = "treatment" + (count + 1);
                db.collection("patient")
                        .document("kbHym7ohbDb80dhv94pzrtFevPX2")
                        .collection("treatments")
                        .document(treatmentId)
                        .set(treatment)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firestore", "Treatment successfully written!");
                        })
                        .addOnFailureListener(e -> {
                            Log.w("Firestore", "Error writing treatment", e);
                        });
            }

            @Override
            public void onCallbackFailed(Exception e) {
                Log.w("Firestore", "Error getting treatment count", e);
            }
        });
    }

    private void getTreatmentCount(OnCountCallback callback) {
        try {
            db.collection("patient")
                    .document("kbHym7ohbDb80dhv94pzrtFevPX2")
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


    public void getTreatments(OnTreatmentsCallback callback) {
        db.collection("patient")
                .document("kbHym7ohbDb80dhv94pzrtFevPX2")
                .collection("treatments")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Treatment> treatments = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        treatments.add(doc.toObject(Treatment.class));
                    }
                    callback.onCallback(treatments);
                })
                .addOnFailureListener(e -> {
                    callback.onCallbackFailed(e);
                });
    }
}
