package it.uniba.dib.sms232417.asilapp.adapters;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.interface_entity.OnDoctorDataCallback;

public class DatabaseAdapterDoctor {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Doctor doctor;
    Context context;
    public DatabaseAdapterDoctor(Context context){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.context = context;
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
                                    Log.d("Register", "onCallback: ");
                                    callback.onCallback(doctor);

                                })
                                .addOnFailureListener(aVoid-> {
                                    Log.d("Register", "onCallbackError: ");
                                    callback.onCallbackError(new Exception(), aVoid.toString());

                                });
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onCallbackError(new Exception(), e.toString());
                });
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
}
