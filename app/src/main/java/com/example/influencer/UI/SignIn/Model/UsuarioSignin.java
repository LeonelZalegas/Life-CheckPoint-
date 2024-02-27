package com.example.influencer.UI.SignIn.Model;

public class UsuarioSignin {
    private String Id;
    private String Email;
    private String Username;
    private final String Contrasena; //agregamos password que es una cosa nueva
    private String profilePictureUrl;
    private Integer years_old;
    private Integer months_old;
    private String countryName;
    private Integer countryFlagResourceId;

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


    public Integer getYears_old() {return years_old;}

    public void setYears_old(Integer years_old) {this.years_old = years_old;}

    public Integer getMonths_old() {return months_old;}

    public void setMonths_old(Integer months_old) {this.months_old = months_old;}

    public String getCountryName() {return countryName;}

    public void setCountryName(String countryName) {this.countryName = countryName;}

    public Integer getCountryFlagResourceId() {return countryFlagResourceId;}

    public void setCountryFlagResourceId(Integer countryFlagResourceId) {this.countryFlagResourceId = countryFlagResourceId;}
}
