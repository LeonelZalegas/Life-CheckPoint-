package com.example.influencer.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.window.SplashScreen;

import com.example.influencer.Providers.AuthProvider;
import com.example.influencer.Providers.UsuariosProvider;
import com.example.influencer.R;
import com.example.influencer.models.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

import android.window.SplashScreen;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    TextView sign_inV;

    EditText ET_poner_email;
    EditText ET_poner_contrasena;
    Button B_Button_LogIn;
    AuthProvider authProvider;

    private GoogleSignInClient mGoogleSignInClient;
    SignInButton B_SignInGoogle;
    UsuariosProvider usuariosProvider;

    SweetAlertDialog carga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //para el splash
        androidx.core.splashscreen.SplashScreen splashScreen = androidx.core.splashscreen.SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> false );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //para el cuadro de loading
        carga = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        carga.getProgressHelper().setBarColor(Color.parseColor("#F57E00"));
        carga.setTitleText(R.string.Loading);
        carga.setCancelable(false);

        //Esto seria para la animacion de fondo de la pantala del LogIn
        ConstraintLayout gradiente_login = findViewById(R.id.gradiente_login);
        AnimationDrawable animacion_login = (AnimationDrawable) gradiente_login.getBackground();

        animacion_login.setEnterFadeDuration(2500);
        animacion_login.setExitFadeDuration(5000);
        animacion_login.start();
        //

        // Para detectar click de registro
        sign_inV = findViewById(R.id.sign_in);
        sign_inV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_registro = new Intent(MainActivity.this,Registro.class);
                startActivity(intent_registro);
            }
        });
        //

        //Para hacer el LogIn

        ET_poner_email = findViewById(R.id.poner_email);
        ET_poner_contrasena = findViewById(R.id.poner_contrasena);
        B_Button_LogIn = findViewById(R.id.button_LogIn);

        authProvider = new AuthProvider();

        B_Button_LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogIn();
            }
        });

        // para empezar el SignIn
        // Configurar Google Sign In
        B_SignInGoogle = findViewById(R.id.buttom_signInGoogle);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ActivityResultLauncher<Intent> resultadoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent intent = result.getData();

                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        Log.d("error", "firebaseAuthWithGoogle:" + account.getId());
                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        // Google Sign In failed, update UI appropriately
                        Log.w("error", "Google sign in failed", e);
                    }
                }
            }
        });

        B_SignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resultadoLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));

            }
        });
    }


    private void firebaseAuthWithGoogle(String idToken) {
        carga.show();
        authProvider.googleSignin(idToken).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Si la autenticacion del usuario tuvo exito entra aca (para guardar ahora en firestore ese usuario)
                            CheckearSiUsuarioExiste();
                        } else {
                            // si llega a fallar algo por alguna razon se muestra este mensaje en el Logcat xd (que es un warning)
                            carga.dismiss();
                            Log.w("ERROR", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    // una vez que el SignIn Con google tuvo exito tenemos que guardar el usuario en la base de datos de Firestore
    private void CheckearSiUsuarioExiste() {
        usuariosProvider = new UsuariosProvider();
        String id = authProvider.getuid();
    // se usa OnSuccessListener porque siempre vamos a tener Exito en la tarea (siempre entrara en el metodo OnSucces) porque siempre podremos acceder al documento con ese id (porq ya fue exitoso el Signin en google) creo yo xd
        usuariosProvider.getusuario(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //preguntamos si ese id existe en firestore (docuemnto con ese id)
                if (documentSnapshot.exists()){
                    carga.dismiss();
                    Intent intent_LogIn = new Intent(MainActivity.this,HomeActivity.class);
                    startActivity(intent_LogIn);
                    finish();
                    //si no existe, la creamos y luego con el OnCompleteListener vemos si se tuvo exito guardando en la base de datos, si es asi, se va a homeactivity
                }else{
                   String Email = authProvider.getemail();
                   // para poner como usuario solo el nombre del email
                    int index = Email.indexOf('@');
                    String Usuarionombre = Email.substring(0,index);
                    //
                    Usuario usuario = new Usuario();
                    usuario.setEmail(Email);
                    usuario.setUsername(Usuarionombre);
                    usuario.setId(id); //antes le metiamos el id adentro del document() pero ahora como en el metodo "crear usuario" ponemos docuement(usuario.getId()) tenemos que si o si setear el id en usuario con el id del authProvider.getuid()
                    usuariosProvider.crearUsuario(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            carga.dismiss();
                            if (task.isSuccessful()){
                                Intent intent_LogIn = new Intent(MainActivity.this,HomeActivity.class);
                                startActivity(intent_LogIn);
                                finish();
                            }else{
                                carga.dismiss();
                                Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }


    protected void LogIn(){
        String email = ET_poner_email.getText().toString();
        String contrasena = ET_poner_contrasena.getText().toString();

        carga.show(); // para mostrar el cuadro de loading

        if( (email != null && !email.trim().isEmpty()) && (contrasena != null && !contrasena.trim().isEmpty()) ){  //esto es para controlar que no se ingresen nulls (no se cargue ningun valor) y tampoco espacios

            authProvider.login(email,contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    carga.dismiss(); //para ya no mostrar el cuadro de loading
                    if (task.isSuccessful()){
                        Intent intent_LogIn = new Intent(MainActivity.this,HomeActivity.class);
                        startActivity(intent_LogIn);
                        finish();
                    }else{
                        Toast.makeText(MainActivity.this, R.string.error_LogIn, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            carga.dismiss();
            Toast.makeText(MainActivity.this, R.string.empty_fields_LogIn, Toast.LENGTH_LONG).show();
        }
   }

}