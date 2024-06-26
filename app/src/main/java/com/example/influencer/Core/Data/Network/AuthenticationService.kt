package com.example.influencer.Core.Data.Network

import com.google.android.gms.tasks.Task
import javax.inject.Singleton
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

@Singleton
class AuthenticationService @Inject constructor(private val firebaseAuth: FirebaseAuth) {

    val uid: String
        get() = firebaseAuth.currentUser?.uid ?: ""

    val email: String
        get() = firebaseAuth.currentUser?.email ?: ""

    fun registrar(email: String, contrasena: String): Task<AuthResult> =
        firebaseAuth.createUserWithEmailAndPassword(email, contrasena)

    fun login(email: String, contrasena: String): Task<AuthResult> =
        firebaseAuth.signInWithEmailAndPassword(email, contrasena)

    fun googleSignin(idToken: String): Task<AuthResult> {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        return firebaseAuth.signInWithCredential(credential)
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}