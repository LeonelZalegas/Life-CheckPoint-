package com.example.influencer.Providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthProvider {

    private FirebaseAuth firebaseAuth;

    public  AuthProvider(){
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> login(String email, String contrasena){
        return firebaseAuth.signInWithEmailAndPassword(email,contrasena);
    }

    public Task<AuthResult> googleSignin (String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        return firebaseAuth.signInWithCredential(credential);
    }
// = que con getemail()
    public String getuid(){
        if (firebaseAuth.getCurrentUser() != null){
            return firebaseAuth.getCurrentUser().getUid();
        }else
            return null;
    }
// la verdad no se que tan necesario es hacer esto xd, tal vez al hacer getEmail() y no haya usuario previamente se rompe todo? porque si te devuelve null,no hace falta hacer el condicional ese
    public String getemail(){
        if (firebaseAuth.getCurrentUser() != null){
            return firebaseAuth.getCurrentUser().getEmail();
        }else
            return null;
    }

    public Task<AuthResult> registrar(String email, String contrasena){
        return firebaseAuth.createUserWithEmailAndPassword(email,contrasena);

    }


}
