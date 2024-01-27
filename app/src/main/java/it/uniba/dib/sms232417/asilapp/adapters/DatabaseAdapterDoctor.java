package it.uniba.dib.sms232417.asilapp.adapters;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;
import it.uniba.dib.sms232417.asilapp.interfaces.OnDoctorDataCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnPatientListDataCallback;

public class DatabaseAdapterDoctor {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Doctor doctor;
    Context context;
    public DatabaseAdapterDoctor(Context context) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    public void onLogin(String email, String password, OnDoctorDataCallback callback){
        Log.d("LOGIN", "inizioMetodo");
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser dottore = mAuth.getCurrentUser();
                    db = FirebaseFirestore.getInstance();

                    db.collection("doctor")
                            .document(dottore.getUid())
                            .get()
                            .addOnSuccessListener(datiUtente-> {
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
                                            if(myPatients == null || myPatients.isEmpty())
                                                Log.d("MyPatients", "Non ce");
                                            if (myPatients != null && !myPatients.isEmpty())
                                                Log.d("MyPatients", myPatients.toString());
                                    callback.onCallback(doctor);
                                }else{
                                    callback.onCallbackError(new Exception(), context.getString(R.string.error_login_section_patient));
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    callback.onCallbackError(new Exception(), e.toString());
                });
    }
    public void onLogout(){
        mAuth.signOut();
    }

    public void getDoctorPatients(List<String> patientUUID, OnPatientListDataCallback callback) {
    db = FirebaseFirestore.getInstance();

    for (String uuid : patientUUID){
        db.collection("patient")
                .whereIn("uuid", Collections.singletonList(uuid))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Patient> patients = queryDocumentSnapshots.toObjects(Patient.class);

                        // Sort patients by name
                        Collections.sort(patients, new Comparator<Patient>() {
                            @Override
                            public int compare(Patient p1, Patient p2) {
                                return p1.getNome().compareTo(p2.getNome());
                            }
                        });

                        callback.onCallback(patients);
                    } else {
                        Log.d("PATIENT", "Error");
                        callback.onCallbackError(new Exception(), "Errore caricamento pazienti");
                    }
                });
    }
}
    public void onLoginQrCode(String token, OnDoctorDataCallback callback){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCustomToken(token)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser dottore = mAuth.getCurrentUser();
                    db = FirebaseFirestore.getInstance();

                    db.collection("doctor")
                            .document(dottore.getUid())
                            .get()
                            .addOnSuccessListener(datiUtente-> {
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
                                    if(myPatients == null || myPatients.isEmpty())
                                        Log.d("MyPatients", "Non ce");
                                    if (myPatients != null && !myPatients.isEmpty())
                                        Log.d("MyPatients", myPatients.toString());
                                    callback.onCallback(doctor);
                                }else{
                                    callback.onCallbackError(new Exception(), "QR Code non valido");
                                    Log.d("Login", "Login non effettuato");
                                }
                            });
                });
    }

    public void addTreatment(Treatment treatment) {

    }
}
