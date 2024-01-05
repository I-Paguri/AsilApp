package it.uniba.dib.sms232417.asilapp.adapters;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseAdapter {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public DatabaseAdapter(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }



}
