package it.uniba.dib.sms232417.asilapp.adapters;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.interface_entity.OnPatientDataCallback;
import it.uniba.dib.sms232417.asilapp.entity.Patient;

public class DatabaseAdapterPatient {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Patient patient;
    Patient resultPatient;
    Context context;

    public DatabaseAdapterPatient(Context context){
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
                                    }else {
                                        callback.onCallbackError(new Exception(),context.getString(R.string.error_login_section_doctor));
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

                        db.collection("users")
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
    public void onLogout(){
        mAuth.signOut();
    }


}
