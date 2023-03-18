package com.example.influencer.Domain;

import androidx.annotation.NonNull;

import com.example.influencer.Data.Network.AuthenticationService;
import com.example.influencer.UI.Login.LoginListener;
import com.example.influencer.UI.Login.Model.UsuarioLogin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class LoginUseCase {
    private final AuthenticationService authenticationService;

    public LoginUseCase(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void invoke(UsuarioLogin usuarioLogin, LoginListener listener) {
        authenticationService.login(usuarioLogin.getEmail(), usuarioLogin.getContrasena()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    listener.LoginSuccess();
                }else{
                    listener.LoginError();
                }
            }
        });
    }
}
