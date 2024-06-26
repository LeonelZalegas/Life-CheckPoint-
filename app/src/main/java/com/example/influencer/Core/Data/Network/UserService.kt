package com.example.influencer.Core.Data.Network

import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin
import com.example.influencer.Core.Data.Network.UserService
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue

class UserService @Inject constructor(private val db: FirebaseFirestore) {

    companion object {
        const val USER_COLLECTION = "Usuarios"
    }

    fun crearUsuario(usuarioSignin: UsuarioSignin): Task<Void> =
        db.collection(USER_COLLECTION).document(usuarioSignin.id).set(usuarioSignin)

    // Este metodo sirve para obtener 1 usuario especifico (osea 1 document dentro de la coleccion usuarios xd) pero es para trabajar
    // unicamente para una sola lectura una sola vez (no es una lectura constante en tiempo real) ya que devuelve un Task como salida
    fun getUsuario(id: String): Task<DocumentSnapshot> =
        db.collection(USER_COLLECTION).document(id).get()

    // Aca hace lo mismo que el de arriba pero si es para lectura constante en tiempo real ya que devuelve simplemente la referencia al Documento
    fun getusuarioRealTime(id: String): DocumentReference =
        db.collection(USER_COLLECTION).document(id)

    fun addCheckpointThemeToUser(id: String, checkpointThemeName: String): Task<Void> =
        db.collection(USER_COLLECTION).document(id)
            .update("CheckpointThemesNames", FieldValue.arrayUnion(checkpointThemeName))
}