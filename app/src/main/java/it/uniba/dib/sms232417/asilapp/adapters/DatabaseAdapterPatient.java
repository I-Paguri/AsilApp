package it.uniba.dib.sms232417.asilapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;
import it.uniba.dib.sms232417.asilapp.interfaces.OnPatientDataCallback;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.interfaces.OnCountCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnProfileImageCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnTreatmentsCallback;

public class DatabaseAdapterPatient extends DatabaseAdapterUser {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Patient patient;
    Patient resultPatient;
    Context context;

    StorageReference patientStorageReference;

    public DatabaseAdapterPatient(Context context) {
        super(context);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.context = context;
        this.patientStorageReference = FirebaseStorage.getInstance().getReference();
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
                                        // Ottieni l'URL dell'immagine del profilo
                                        getProfileImage(utente.getUid(), new OnProfileImageCallback() {
                                            @Override
                                            public void onCallback(String imageUrl) {
                                                // Crea un nuovo oggetto Patient con l'URL dell'immagine del profilo
                                                resultPatient = new Patient(utente.getUid(), datiUtente.getString("nome"),
                                                        datiUtente.getString("cognome"),
                                                        datiUtente.getString("email"),
                                                        datiUtente.getString("dataNascita"),
                                                        datiUtente.getString("regione"),
                                                        imageUrl);
                                                callback.onCallback(resultPatient);
                                            }

                                            @Override
                                            public void onCallbackError(Exception e) {
                                                // Gestisci l'errore
                                                Log.e("Firestore", "Error getting profile image", e);
                                            }
                                        });
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

    public void onRegister(String nome, String cognome, String email, String dataNascita, String regione, String profileImageUrl, String password, OnPatientDataCallback callback) {

        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser utente = mAuth.getCurrentUser();
                        db = FirebaseFirestore.getInstance();
                        Log.d("REGISTER", "Registrazione effettuata con successo");


                        patient = new Patient(utente.getUid(), nome, cognome, email, dataNascita, regione, profileImageUrl);

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

    /*
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
     */

    /*
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
    */

    /*
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

     */

    public void connectToContainer(String token, String patientUUID, boolean isConnect) {

        db.collection("qr_code_container")
                .document(token)
                .update("uuid", patientUUID, "isConnect", isConnect)
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

    public void setFlagContainer(boolean flag, String token) {
        db.collection("qr_code_container")
                .document(token)
                .update("isConnect", flag)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Flag successfully set!");
                })
                .addOnFailureListener(e -> {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                    builder.setTitle("Errore");
                    builder.setMessage(e.getMessage());
                    builder.setPositiveButton("OK", null);
                });
    }

    public void updateProfileImage(String userUUID, String imageUrl) {
        db.collection("patient")
                .document(userUUID)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Profile image successfully updated!");
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error updating profile image", e);
                });
    }

    public void getProfileImage(String userUUID, OnProfileImageCallback callback) {
        db.collection("patient")
                .document(userUUID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                        callback.onCallback(profileImageUrl);
                    } else {
                        callback.onCallbackError(new Exception("No profile image found for this user."));
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onCallbackError(e);
                });
    }

    public void uploadImage(Context context, Bitmap bitmap, String userUUID) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference ref = patientStorageReference.child("images/" + userUUID + "/" + UUID.randomUUID().toString());

        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // Get the download URL
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Do something with the URL
                    }
                });
            }
        });
    }

}
