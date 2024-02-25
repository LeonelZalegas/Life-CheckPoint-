package com.example.influencer.Domain.GoogleSigninUSECASES;

import com.example.influencer.Data.Network.AuthenticationService;
import com.example.influencer.Data.Network.UserService;
import com.example.influencer.UI.SignIn.Model.UsuarioSignin;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

public class CreateGoogleUserUseCase {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Inject
    public CreateGoogleUserUseCase(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    public Task<Void> execute(String profilePictureUrl) {
        String email = authenticationService.getEmail();
        int index = email.indexOf('@');
        String username = email.substring(0, index);
        UsuarioSignin usuarioSignin = new UsuarioSignin(email, username, "NO PASSWORD SAVED WITH GOOGLE SIGNIN");
        usuarioSignin.setId(authenticationService.getUid());
        usuarioSignin.setProfilePictureUrl(profilePictureUrl);
        return userService.crearUsuario(usuarioSignin);
    }
}