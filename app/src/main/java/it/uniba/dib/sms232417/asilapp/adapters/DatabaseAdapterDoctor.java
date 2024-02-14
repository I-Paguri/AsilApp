package it.uniba.dib.sms232417.asilapp.adapters;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Collections;
import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;
import it.uniba.dib.sms232417.asilapp.interfaces.OnDoctorDataCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnPatientListDataCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnProfileImageCallback;

public class DatabaseAdapterDoctor extends DatabaseAdapterUser {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Doctor doctor;
    Context context;

    public DatabaseAdapterDoctor(Context context) {
        super(context);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    public void onLogin(String email, String password, OnDoctorDataCallback callback) {
        Log.d("LOGIN", "inizioMetodo");
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser dottore = mAuth.getCurrentUser();
                    db = FirebaseFirestore.getInstance();

                    db.collection("doctor")
                            .document(dottore.getUid())
                            .get()
                            .addOnSuccessListener(datiUtente -> {
                                if (datiUtente.exists()) {
                                    Log.d("Login", "Login in corso");
                                    doctor = new Doctor(datiUtente.getString("nome"),
                                            datiUtente.getString("cognome"),
                                            datiUtente.getString("email"),
                                            datiUtente.getString("dataNascita"),
                                            datiUtente.getString("regione"),
                                            datiUtente.getString("specializzazione"),
                                            datiUtente.getString("numeroDiRegistrazioneMedica"));

                                    List<String> myPatients = (List<String>) datiUtente.get("myPatients");
                                    doctor.setMyPatientsUUID(myPatients);
                                    if (myPatients == null || myPatients.isEmpty())
                                        Log.d("MyPatients", "Non ce");
                                    if (myPatients != null && !myPatients.isEmpty())
                                        Log.d("MyPatients", myPatients.toString());
                                    callback.onCallback(doctor);
                                } else {
                                    callback.onCallbackError(new Exception(), context.getString(R.string.error_login_section_patient));
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    callback.onCallbackError(new Exception(), e.toString());
                });
    }

    public void onLogout() {
        mAuth.signOut();
    }

    public void getDoctorPatients(List<String> patientUUID, OnPatientListDataCallback callback) {
        db = FirebaseFirestore.getInstance();

        for (String uuid : patientUUID) {
            db.collection("patient")
                    .whereIn("uuid", Collections.singletonList(uuid))
                    .orderBy("nome", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<Patient> patients = queryDocumentSnapshots.toObjects(Patient.class);

                            callback.onCallback(patients);
                        } else {
                            Log.d("PATIENT", "Error");
                            callback.onCallbackError(new Exception(), "Errore caricamento pazienti");
                        }
                    });
        }
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

    public void onLoginQrCode(String token, OnDoctorDataCallback callback) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCustomToken(token)
                .addOnSuccessListener(authResult -> {
                    Log.d("Login", "Login in corso");
                    FirebaseUser dottore = mAuth.getCurrentUser();
                    db = FirebaseFirestore.getInstance();

                    db.collection("doctor")
                            .document(dottore.getUid())
                            .get()
                            .addOnSuccessListener(datiUtente -> {
                                if (datiUtente.exists()) {
                                    Log.d("Login", "Login in corso");
                                    doctor = new Doctor(datiUtente.getString("nome"),
                                            datiUtente.getString("cognome"),
                                            datiUtente.getString("email"),
                                            datiUtente.getString("dataNascita"),
                                            datiUtente.getString("regione"),
                                            datiUtente.getString("specializzazione"),
                                            datiUtente.getString("numeroDiRegistrazioneMedica"));

                                    List<String> myPatients = (List<String>) datiUtente.get("myPatients");
                                    doctor.setMyPatientsUUID(myPatients);
                                    if (myPatients == null || myPatients.isEmpty())
                                        Log.d("MyPatients", "Non ce");
                                    if (myPatients != null && !myPatients.isEmpty())
                                        Log.d("MyPatients", myPatients.toString());
                                    callback.onCallback(doctor);
                                } else {
                                    callback.onCallbackError(new Exception(), "QR Code non valido");
                                    Log.d("Login", "Login non effettuato");
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Log.d("Login", "Login non effettuato");
                    callback.onCallbackError(new Exception(), e.toString());
                });
    }

    public void updateProfileImage(String email, String imageUrl) {
        db.collection("doctor")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String docId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        db.collection("doctor")
                                .document(docId)
                                .update("profileImageUrl", imageUrl)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Profile image successfully updated!");
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("Firestore", "Error updating profile image", e);
                                });
                    }
                });
    }

    public void getProfileImage(String email, OnProfileImageCallback callback) {
        db.collection("doctor")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String profileImageUrl = queryDocumentSnapshots.getDocuments().get(0).getString("profileImageUrl");
                        callback.onCallback(profileImageUrl);
                    } else {
                        callback.onCallbackError(new Exception("No profile image found for this user."));
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onCallbackError(e);
                });
    }

}
