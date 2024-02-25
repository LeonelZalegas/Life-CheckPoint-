package com.example.influencer.UI.SignIn.Model;

public class UsuarioSignin {
    private String Id;
    private String Email;
    private String Username;
    private final String Contrasena; //agregamos password que es una cosa nueva
    private String profilePictureUrl;

    //contructor ahora no tiene  Id
    public UsuarioSignin(String email, String username,String contrasena) {
        Email = email;
        Username = username;
        Contrasena = contrasena;
    }

    //getters y setters
    public String getContrasena() {
        return Contrasena;
    }

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

    public void setUsername(String username) {
        Username = username;
    }

    public String getUsername(){return Username;}

    public String getProfilePictureUrl() {return profilePictureUrl;}

    public void setProfilePictureUrl(String profilePictureUrl) {this.profilePictureUrl = profilePictureUrl;}
}
