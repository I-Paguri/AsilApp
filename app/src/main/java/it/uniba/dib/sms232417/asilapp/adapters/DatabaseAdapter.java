package it.uniba.dib.sms232417.asilapp.adapters;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.interface_entity.OnDoctorDataCallback;
import it.uniba.dib.sms232417.asilapp.entity.interface_entity.OnPatientDataCallback;
import it.uniba.dib.sms232417.asilapp.entity.Patient;

public class DatabaseAdapter {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Patient patient;
    Patient resultPatient;

    Doctor doctor;

    public DatabaseAdapter(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void onLogin(String emailIns, String password, OnPatientDataCallback callback) {

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(emailIns, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser utente = mAuth.getCurrentUser();
                        db = FirebaseFirestore.getInstance();
                        Log.d("LOGIN", "Login effettuato con successo");

                        db.collection("users")
                                .document(utente.getUid())
                                .get()
                                .addOnSuccessListener(datiUtente-> {
                                    if (datiUtente.exists()) {
                                        resultPatient = new Patient(datiUtente.getString("nome"),
                                                datiUtente.getString("cognome"),
                                                datiUtente.getString("email"),
                                                datiUtente.getString("dataNascita"),
                                                datiUtente.getString("regione"));
                                        callback.onCallback(resultPatient);
                                    }
                                });
                    }
                })
                .addOnFailureListener(task -> {
                    callback.onCallbackError(new Exception());

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

                        db.collection("users")
                                .document(utente.getUid())
                                .set(patient)
                                .addOnSuccessListener(aVoid -> {
                                    callback.onCallback(patient);
                                });
                    }
                })
                .addOnFailureListener(task -> {
                    callback.onCallbackError(new Exception());
                });
    }
    public void onLogout(){
        mAuth.signOut();
    }

    public void onRegistrationDoctor(String nome, String cognome, String email, String dataNascita, String regione, String specializzazione, String numeroDiRegistrazioneMedica, String password, OnDoctorDataCallback callback) {

        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser dottore = mAuth.getCurrentUser();
                        db = FirebaseFirestore.getInstance();
                        Log.d("REGISTER", "Registrazione effettuata con successo");

                        doctor = new Doctor(nome, cognome, email, dataNascita, regione, specializzazione, numeroDiRegistrazioneMedica);
                        db.collection("doctor")
                                .document(dottore.getUid())
                                .set(doctor)
                                .addOnSuccessListener(aVoid -> {
                                    callback.onCallback(doctor);
                                })
                                .addOnFailureListener(aVoid-> {
                                callback.onCallbackError(new Exception());
                                });
                    }
                });

    }
    public void onLoginDoctor(String email, String password, OnDoctorDataCallback callback) {
        /*
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser doctor = mAuth.getCurrentUser();
                        db = FirebaseFirestore.getInstance();
                        Log.d("LOGIN", "Login effettuato con successo");

                        db.collection("users")
                                .document(doctor.getUid())
                                .get()
                                .addOnSuccessListener(datiUtente-> {
                                    if (datiUtente.exists()) {
                                        resultPatient = new Patient(datiUtente.getString("nome"),
                                                datiUtente.getString("cognome"),
                                                datiUtente.getString("email"),
                                                datiUtente.getString("dataNascita"),
                                                datiUtente.getString("regione"));
                                        callback.onCallback(resultPatient);
                                    }
                                });
                    }
                })
                .addOnFailureListener(task -> {
                    callback.onCallbackError(new Exception());

                });

         */
    }

}
