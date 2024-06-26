package com.example.influencer.Core.Data.Network;

import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

public class UserService {
    public static final String USER_COLLECTION = "Usuarios";
    private final FirebaseFirestore db;

    @Inject
    public UserService(FirebaseFirestore db) {
        this.db = db;;
    }

    public Task<Void> crearUsuario(UsuarioSignin usuarioSignin){
        return  db.collection(USER_COLLECTION).document(usuarioSignin.getId()).set(usuarioSignin);
    }

    //este metodo sirve para obtener 1 usuario especifico(osea 1 document dentro de la coleccion usuarios xd) pero es para trabajar
    //unicamente para una sola lectura una sola vez(no es una lectura constante en tiempo real)ya que devuelve un Task como salida
    public Task<DocumentSnapshot> getUsuario(String id){
        return db.collection(USER_COLLECTION).document(id).get();
    }

    //Aca hace lo mismo que el de arriva pero si es para lectura constante en tiempo real ya que devuelve simplemente la referencia al Documento
    public DocumentReference getusuarioRealTime(String id) {
        return db.collection(USER_COLLECTION).document(id);
    }

    public Task<Void> addCheckpointThemeToUser (String id, String checkpointThemeName){
        return  db.collection(USER_COLLECTION).document(id).update("CheckpointThemesNames", FieldValue.arrayUnion(checkpointThemeName));
    }
}
