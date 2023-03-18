package com.example.influencer.Domain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.influencer.Data.Network.AuthenticationService;
import com.example.influencer.Data.Network.UserService;
import com.example.influencer.R;
import com.example.influencer.UI.GoogleSignin.Callback;
import com.example.influencer.UI.SignIn.Model.UsuarioSignin;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GoogleSigninUseCase {

    SweetAlertDialog carga;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    Context context;
    AppCompatActivity googleSigninActivity;

    public GoogleSigninUseCase(AuthenticationService authenticationService, UserService userService, Context context,AppCompatActivity googleSigninActivity ){
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.context = context;
        this.googleSigninActivity = googleSigninActivity;

        //para el cuadro de loading
        carga = new SweetAlertDialog(context,SweetAlertDialog.PROGRESS_TYPE);
        carga.getProgressHelper().setBarColor(Color.parseColor("#F57E00"));
        carga.setTitleText(R.string.Loading);
        carga.setCancelable(false);
    }

    public void signInWithGoogle(Intent signInIntent, Callback callback) {
        ActivityResultLauncher<Intent> resultadoLauncher = googleSigninActivity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();

                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                            try {
                                // Google Sign In was successful, authenticate with Firebase
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                Log.d("error", "firebaseAuthWithGoogle:" + account.getId());
                                firebaseAuthWithGoogle(account.getIdToken(), callback);
                            } catch (ApiException e) {
                                // Google Sign In failed, update UI appropriately
                                Log.w("error", "Google sign in failed", e);
                                callback.onError();
                            }
                        }
                    }
                });

        resultadoLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken, Callback callback) {
        carga.show();
        authenticationService.googleSignin(idToken).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Si la autenticacion del usuario tuvo exito entra aca (para guardar ahora en firestore ese usuario)
                    CheckearSiUsuarioExiste(callback);
                } else {
                    // si llega a fallar algo por alguna razon se muestra este mensaje en el Logcat xd (que es un warning)
                    carga.dismiss();
                    Log.w("ERROR", "signInWithCredential:failure", task.getException());
                    callback.onError();
                }
            }
        });
    }

    // una vez que el SignIn Con google tuvo exito tenemos que guardar el usuario en la base de datos de Firestore
    private void CheckearSiUsuarioExiste(Callback callback) {
        String id = authenticationService.getuid();
        // se usa OnSuccessListener porque siempre vamos a tener Exito en la tarea (siempre entrara en el metodo OnSucces) porque siempre podremos acceder al documento con ese id (porq ya fue exitoso el Signin en google) creo yo xd
        userService.getusuario(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //preguntamos si ese id existe en firestore (docuemnto con ese id)
                if (documentSnapshot.exists()) {
                    carga.dismiss();
                    callback.onSuccess();
                    //si no existe, la creamos y luego con el OnCompleteListener vemos si se tuvo exito guardando en la base de datos, si es asi, se va a homeactivity
                } else {
                    String Email = authenticationService.getemail();
                    // para poner como usuario solo el nombre del email
                    int index = Email.indexOf('@');
                    String Usuarionombre = Email.substring(0, index);
                    //
                    UsuarioSignin usuarioSignin = new UsuarioSignin(Email,Usuarionombre,"NO PASSWORD SAVED WITH GOOGLE SIGNIN");
                    usuarioSignin.setId(id);
                    userService.crearUsuario(usuarioSignin).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            carga.dismiss();
                            if (task.isSuccessful()) {
                                callback.onSuccess();
                            } else {
                                callback.onError();
                            }
                        }
                    });
                }
            }
        });
    }
}
