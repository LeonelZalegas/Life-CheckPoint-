package com.example.influencer.Domain;

import androidx.annotation.NonNull;

import com.example.influencer.Data.Network.AuthenticationService;
import com.example.influencer.UI.Login.LoginListener;
import com.example.influencer.UI.Login.Model.UsuarioLogin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import javax.inject.Inject;

//aca lo 100% ideal seria o ya sea usar RxJava (un Single (equivalante al floawble pero para 1 unica operacion asyncronica nada mas), Completable, maybe) o corrutinas de Kotlin
//ya que al usar callBacks si bien el UseCase no maneja la implementacion de "task.isSuccessful()" si es conciente de la existencia de la interfaz (Listener), que seria un componente de
//que lo implementa el viewmodel, aun asi, dentro de todo sigue cumpliendo un MVVM con clean Architecure ya que depsues de todo no sabe ninguna implementacion (todo lo hace el viewmodel)
public class LoginUseCase {
    private final AuthenticationService authenticationService;

    @Inject
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
