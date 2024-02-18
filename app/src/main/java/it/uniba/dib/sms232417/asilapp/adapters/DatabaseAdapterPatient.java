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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.entity.Expenses;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;
import it.uniba.dib.sms232417.asilapp.interfaces.OnExpensesListCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnPatientDataCallback;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.interfaces.OnCountCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnProfileImageCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnTotalExpensesCallback;
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
        db.clearPersistence();
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
                        Log.d("MyAccountFragment", "Profile image URL: " + profileImageUrl);
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
                        String imageUrl = uri.toString();
                        callback.onCallback(imageUrl);
                    }
                });
            }
        });
    }

    public void getPatient(String patientUUID, OnPatientDataCallback callback) {
        db.collection("patient")
                .document(patientUUID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Patient patient = new Patient(documentSnapshot.getString("UUID"),
                                documentSnapshot.getString("nome"),
                                documentSnapshot.getString("cognome"),
                                documentSnapshot.getString("email"),
                                documentSnapshot.getString("dataNascita"),
                                documentSnapshot.getString("regione"),
                                documentSnapshot.getString("profileImageUrl"));
                        callback.onCallback(patient);
                    } else {
                        callback.onCallbackError(new Exception(), "No patient found with this UUID.");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onCallbackError(e, "Error getting patient");
                });
    }
    public void addExpense(String patientUUID, Expenses newExpense) {
        // Convert the Expenses object into a Map that can be stored in Firestore
        Map<String, Object> expenseMap = new HashMap<>();
        expenseMap.put("Category", newExpense.getCategory().toString());
        expenseMap.put("amount", newExpense.getAmount());
        expenseMap.put("date", newExpense.getDate());

        // Get the patient's document
        DocumentReference patientDocument = db.collection("patient").document(patientUUID);

        // Retrieve the current list of expenses, add the new expense, and update the document
        patientDocument.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<Map<String, Object>> expensesList = (List<Map<String, Object>>) document.get("expenses");
                    if (expensesList == null) {
                        expensesList = new ArrayList<>();
                    }
                    expensesList.add(expenseMap);
                    patientDocument.update("expenses", expensesList)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Expense successfully added!"))
                            .addOnFailureListener(e -> Log.w("Firestore", "Error adding expense", e));
                } else {
                    Log.d("Firestore", "No such document");
                }
            } else {
                Log.d("Firestore", "get failed with ", task.getException());
            }
        });
    }

    public void getExpensesList(String patientUUID, OnExpensesListCallback callback) {
        db.collection("patient")
                .document(patientUUID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> firestoreExpensesList = (List<Map<String, Object>>) documentSnapshot.get("expenses");
                        List<Expenses> expensesList = new ArrayList<>();
                        for (Map<String, Object> expenseMap : firestoreExpensesList) {
                            Expenses.Category category = Expenses.Category.valueOf((String) expenseMap.get("Category"));
                            double amount = (double) expenseMap.get("amount");
                            // Convert the Timestamp to a Date
                            Timestamp timestamp = (Timestamp) expenseMap.get("date");
                            Date date = timestamp.toDate();
                            expensesList.add(new Expenses(category, amount, date));
                        }
                        callback.onCallback(expensesList);
                    } else {
                        callback.onCallbackError(new Exception("No patient found with this UUID."));
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onCallbackError(e);
                });
    }

    public void getSumExpenses(String patientUUID, OnTotalExpensesCallback callback) {
        db.collection("patient")
                .document(patientUUID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> firestoreExpensesList = (List<Map<String, Object>>) documentSnapshot.get("expensesList");
                        List<Expenses> expensesList = new ArrayList<>();
                        for (Map<String, Object> expenseMap : firestoreExpensesList) {
                            expensesList.add(Expenses.fromMap(expenseMap));
                            Log.d("Firestore", "Expense: " + expenseMap);
                        }

                        double totalExpenses = 0.0;
                        for (Expenses expense : expensesList) {
                            totalExpenses += expense.getAmount();
                        }

                        callback.onCallback(totalExpenses);
                    } else {
                        callback.onCallbackError(new Exception("No patient found with this UUID."));
                        Log.e("Firestore", "No patient found with this UUID.");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onCallbackError(e);
                });
    }

}
