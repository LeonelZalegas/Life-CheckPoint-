package com.example.influencer.Data.Network;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthenticationService {

    private static AuthenticationService instance;
    private final FirebaseClient firebase;

    private AuthenticationService(FirebaseClient firebase) {
        this.firebase = firebase;
    }
    //singleton
    public static AuthenticationService getInstance() {
        if (instance == null) {
            FirebaseClient firebase = FirebaseClient.getInstance();
            instance = new AuthenticationService(firebase);
        }
        return instance;
    }

    public String getuid(){
        FirebaseUser currentUser = firebase.getfirebaseAuth().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        }else
            return null;
    }


    public String getemail(){
        FirebaseUser currentUser = firebase.getfirebaseAuth().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getEmail();
        }else
            return null;
    }


    public Task<AuthResult> registrar(String email, String contrasena){
        return firebase.getfirebaseAuth().createUserWithEmailAndPassword(email,contrasena);
    }

    public Task<AuthResult> login(String email, String contrasena){
        return firebase.getfirebaseAuth().signInWithEmailAndPassword(email,contrasena);
    }

    public Task<AuthResult> googleSignin (String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        return firebase.getfirebaseAuth().signInWithCredential(credential);
    }
}

