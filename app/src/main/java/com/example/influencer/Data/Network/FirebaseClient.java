package com.example.influencer.Data.Network;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseClient {
    private static FirebaseClient instance;
    private FirebaseAuth firebaseAuth;
    private final FirebaseFirestore db;


    private FirebaseClient() {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    //para el singleton (solo utilizan esta clase:AuthenticationService y UserService)
    public static FirebaseClient getInstance() {
        if (instance == null) {
            instance = new FirebaseClient();
        }
        return instance;
    }

    public FirebaseAuth getfirebaseAuth() {
        return firebaseAuth;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

}
