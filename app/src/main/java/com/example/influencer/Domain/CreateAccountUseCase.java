package com.example.influencer.Domain;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.influencer.Data.Network.AuthenticationService;
import com.example.influencer.Data.Network.UserService;
import com.example.influencer.R;
import com.example.influencer.UI.SignIn.CreateAccountListener;
import com.example.influencer.UI.SignIn.Model.UsuarioSignin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class CreateAccountUseCase {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public CreateAccountUseCase(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    public void invoke(UsuarioSignin usuarioSignIn, CreateAccountListener listener, Context context) {
        authenticationService.registrar(usuarioSignIn.getEmail(), usuarioSignIn.getContrasena()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    usuarioSignIn.setId(authenticationService.getuid());
                    userService.crearUsuario(usuarioSignIn).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, R.string.LogIn_successful, Toast.LENGTH_LONG).show();
                                listener.onCreateAccountSuccess();
                            } else {
                                Toast.makeText(context, "Error creating firestore user", Toast.LENGTH_LONG).show();
                                listener.onCreateAccountError();
                            }
                        }
                    });
                } else {
                    Toast.makeText(context, R.string.error_SignIn, Toast.LENGTH_LONG).show();
                    listener.onCreateAccountError();
                }
            }
        });
    }
}
