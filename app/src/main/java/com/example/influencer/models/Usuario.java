package com.example.influencer.models;

//vendria a representar la coleccion de "Usuarios" en nuestra base de datos en Firestore

// Model: representación de la información con la cual el sistema opera, por lo tanto gestiona todos los accesos a dicha información, tanto consultas como actualizaciones,
// implementando también los privilegios de acceso que se hayan descrito en las especificaciones de la aplicación

public class Usuario {

    private String Id;
    private String Email;
    private String Username;

    public  Usuario(){
        //vacio
    }

    public Usuario(String id, String email, String username) {
        Id = id;
        Email = email;
        Username = username;
    }

    //getters y setters
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }
}
