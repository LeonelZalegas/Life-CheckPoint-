package com.example.influencer.Domain;

import androidx.annotation.NonNull;

import com.example.influencer.Data.Network.AuthenticationService;
import com.example.influencer.Data.Network.UserService;
import com.example.influencer.UI.SignIn.AppSignIn.CreateAccountListener;
import com.example.influencer.UI.SignIn.Model.UsuarioSignin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import javax.inject.Inject;

//lo mismo que dije para el UseCase del Login
public class CreateAccountUseCase {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Inject
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

                    // Set the default profile picture URL
                    String defaultProfilePicUrl = "https://firebasestorage.googleapis.com/v0/b/life-checkpoint.appspot.com/o/default_profile_picture.jpg?alt=media&token=e8f39ca1-adf9-4953-aa1b-6322889403fa";
                    usuarioSignIn.setProfilePictureUrl(defaultProfilePicUrl);

                    // Proceed with creating the user document in Firestore
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
