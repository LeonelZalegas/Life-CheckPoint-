package com.example.influencer.Features.Login.Domain.Model;

public class UsuarioLogin {
    private String Email;
    private final String Contrasena;


    public UsuarioLogin(String email,String contrasena) {
        Email = email;
        Contrasena = contrasena;
    }

    //getters y setters
    public String getContrasena() {
        return Contrasena;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
