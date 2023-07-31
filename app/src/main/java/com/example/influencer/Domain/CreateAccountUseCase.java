package com.example.influencer.Domain;

import androidx.annotation.NonNull;

import com.example.influencer.Data.Network.AuthenticationService;
import com.example.influencer.Data.Network.UserService;
import com.example.influencer.UI.SignIn.AppSignIn.CreateAccountListener;
import com.example.influencer.UI.SignIn.Model.UsuarioSignin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

//lo mismo que dije para el UseCase del Login
public class CreateAccountUseCase {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public CreateAccountUseCase(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    public void invoke(UsuarioSignin usuarioSignIn, CreateAccountListener listener) {
        authenticationService.registrar(usuarioSignIn.getEmail(), usuarioSignIn.getContrasena()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {  //si autenticarse tiene exito ingresa aca (para crear y guardar el usuario en firestore)
                    usuarioSignIn.setId(authenticationService.getUid());
                    userService.crearUsuario(usuarioSignIn).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                listener.onCreateAccountSuccess();
                            } else {
                                listener.onCreateAccountError();
                            }
                        }
                    });
                } else {
                    listener.onCreateAccountError();
                }
            }
        });
    }
}
