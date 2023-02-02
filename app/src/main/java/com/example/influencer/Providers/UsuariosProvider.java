package com.example.influencer.Providers;

//Logica para almacenar,obtener,actualizar o eliminar datos de firestore (relacionado con el documento "Usuarios")

import com.example.influencer.models.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsuariosProvider{

    private CollectionReference collectionReference;

    public UsuariosProvider(){
        collectionReference = FirebaseFirestore.getInstance().collection("Usuarios");
    }

    public Task<DocumentSnapshot> getusuario(String id){
        return collectionReference.document(id).get();
    }

    public Task<Void> crearUsuario(Usuario usuario){
        return  collectionReference.document(usuario.getId()).set(usuario);
    }


}
