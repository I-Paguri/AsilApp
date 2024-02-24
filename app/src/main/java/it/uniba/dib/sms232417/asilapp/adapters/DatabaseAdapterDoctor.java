package it.uniba.dib.sms232417.asilapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.interfaces.OnDoctorDataCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnPatientListDataCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnProfileImageCallback;

public class DatabaseAdapterDoctor extends DatabaseAdapterUser {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Doctor doctor;
    Context context;

    StorageReference doctorStorageReference;


    public DatabaseAdapterDoctor(Context context) {
        super(context);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.context = context;
        this.doctorStorageReference = FirebaseStorage.getInstance().getReference();
    }

    public void onLogin(String email, String password, OnDoctorDataCallback callback) {
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
                                    doctor = new Doctor(datiUtente.getString("uuid"), datiUtente.getString("nome"),
                                            datiUtente.getString("cognome"),
                                            datiUtente.getString("email"),
                                            datiUtente.getString("dataNascita"),
                                            datiUtente.getString("regione"),
                                            datiUtente.getString("specializzazione"),
                                            datiUtente.getString("numeroDiRegistrazioneMedica"));

                                    List<String> myPatients = (List<String>) datiUtente.get("myPatients");
                                    doctor.setMyPatients(myPatients);
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

    public void onLogout() {
        mAuth.signOut();
    }

    public void getDoctorPatients(List<String> patientUUID, OnPatientListDataCallback callback) {
        db = FirebaseFirestore.getInstance();

        for (String uuid : patientUUID) {
            db.collection("patient")
                    .whereIn("uuid", Collections.singletonList(uuid))
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<Patient> patients = queryDocumentSnapshots.toObjects(Patient.class);

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
                                    doctor = new Doctor(datiUtente.getString("uuid"), datiUtente.getString("nome"),
                                            datiUtente.getString("cognome"),
                                            datiUtente.getString("email"),
                                            datiUtente.getString("dataNascita"),
                                            datiUtente.getString("regione"),
                                            datiUtente.getString("specializzazione"),
                                            datiUtente.getString("numeroDiRegistrazioneMedica"));

                                    List<String> myPatients = (List<String>) datiUtente.get("myPatients");
                                    doctor.setMyPatients(myPatients);
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

                        //String profileImageUrl = queryDocumentSnapshots.getDocuments().get(0).getString("profileImageUrl");
                        //Log.d("MyAccountFragment", "Profile image URL: " + profileImageUrl);
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

    public void uploadImage(Context context, Bitmap bitmap, String userUUID, OnProfileImageCallback callback) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference ref = doctorStorageReference.child("images/" + userUUID + "/" + UUID.randomUUID().toString());

        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                callback.onCallbackError(exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // Get the download URL and pass it to the callback
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        callback.onCallback(uri.toString());
                    }
                });
            }
        });
    }

    public void getDoctor(String doctorUUID, OnDoctorDataCallback callback) {
        db.collection("doctor")
                .document(doctorUUID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Doctor doctor = documentSnapshot.toObject(Doctor.class);
                        callback.onCallback(doctor);
                    } else {
                        callback.onCallbackError(new Exception("No doctor found with this UUID."), "No doctor found with this UUID.");
                    }
                }).addOnFailureListener(e -> {
                    callback.onCallbackError(e, e.toString());
                });
    }
}
