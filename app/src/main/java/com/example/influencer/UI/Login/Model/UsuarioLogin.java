package com.example.influencer.UI.Login.Model;

public class UsuarioLogin {
    private String Email;
    private String Contrasena;

    public  UsuarioLogin(){
        //vacio
    }

    public UsuarioLogin(String email,String contrasena) {
        Email = email;
        Contrasena = contrasena;
    }

    //getters y setters
    public String getContrasena() {
        return Contrasena;
    }

    public void setContrasena(String contrasena) {
        Contrasena = contrasena;
    }


    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
