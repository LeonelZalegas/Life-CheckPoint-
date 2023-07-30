package com.example.influencer.Domain;

import com.example.influencer.Data.Network.AuthenticationService;
import com.example.influencer.Data.Network.UserService;
import com.example.influencer.UI.SignIn.Model.UsuarioSignin;
import com.google.android.gms.tasks.Task;

public class CreateGoogleUserUseCase {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public CreateGoogleUserUseCase(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    public Task<Void> execute() {
        String email = authenticationService.getEmail();
        int index = email.indexOf('@');
        String username = email.substring(0, index);
        UsuarioSignin usuarioSignin = new UsuarioSignin(email, username, "NO PASSWORD SAVED WITH GOOGLE SIGNIN");
        usuarioSignin.setId(authenticationService.getUid());
        return userService.crearUsuario(usuarioSignin);
    }
}