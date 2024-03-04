package com.example.influencer.UI.SignIn.Model;

import com.google.firebase.firestore.DocumentId;

public class UsuarioSignin {
    private String Email;
    private String Username;
    private String Contrasena; //agregamos password que es una cosa nueva
    private String profilePictureUrl;
    private Integer years_old;
    private Integer months_old;
    private String countryName;
    private Integer countryFlagResourceId;
    private Integer postCount = 0 ;
    private Integer followersCount = 0;
    private Integer followingCount = 0;
    @DocumentId         //con esto populamos el ID al pasar de documentos de Firestore del tipo UsuarioSignin a objetos en si del tipo UsuarioSignin
    private String Id; //By default, when you use toObject() (ver FirestoreUserRepository en getRandomUserDocument) to parse a Firestore document into a Kotlin object, it only converts the fields within the document and does not include the document ID because the ID is metadata, not part of the document's content.

    //contructor ahora no tiene  Id
    public UsuarioSignin(String email, String username,String contrasena) {
        Email = email;
        Username = username;
        Contrasena = contrasena;
    }

    public UsuarioSignin() {
    }

    //getters y setters
    public String getContrasena() {
        return Contrasena;
    }

    public void setContrasena(String contrasena) {Contrasena = contrasena;}

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

    public Integer getPostCount() {return postCount;}

    public void setPostCount(Integer postCount) {this.postCount = postCount;}

    public Integer getFollowersCount() {return followersCount;}

    public void setFollowersCount(Integer followersCount) {this.followersCount = followersCount;}

    public Integer getFollowingCount() {return followingCount;}

    public void setFollowingCount(Integer followingCount) {this.followingCount = followingCount;}

}
