package com.example.influencer.Data.Network;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthenticationService {

    private final FirebaseAuth firebaseAuth;

    @Inject
    private AuthenticationService(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public String getUid(){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        }else
            return null;
    }

    public String getEmail(){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getEmail();
        }else
            return null;
    }

    public Task<AuthResult> registrar(String email, String contrasena){
        return firebaseAuth.createUserWithEmailAndPassword(email,contrasena);
    }

    public Task<AuthResult> login(String email, String contrasena){
        return firebaseAuth.signInWithEmailAndPassword(email,contrasena);
    }

    public Task<AuthResult> googleSignin (String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        return firebaseAuth.signInWithCredential(credential);
    }
}

