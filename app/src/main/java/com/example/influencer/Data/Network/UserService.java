package com.example.influencer.Data.Network;

import com.example.influencer.UI.SignIn.Model.UsuarioSignin;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserService {
    public static final String USER_COLLECTION = "Usuarios";
    private final FirebaseClient firebase;

    public UserService(FirebaseClient firebase) {
        this.firebase = firebase;
    }

    public Task<Void> crearUsuario(UsuarioSignin usuarioSignin){
        return  firebase.getDb().collection(USER_COLLECTION).document(usuarioSignin.getId()).set(usuarioSignin);
    }

    public Task<DocumentSnapshot> getusuario(String id){
        return firebase.getDb().collection(USER_COLLECTION).document(id).get();
    }
}
