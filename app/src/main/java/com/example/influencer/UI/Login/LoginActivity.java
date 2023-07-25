package com.example.influencer.UI.Login;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.influencer.Core.Event;
import com.example.influencer.Data.Preferences.UserPreferences;
import com.example.influencer.UI.Home;
import com.example.influencer.R;
import com.example.influencer.UI.GoogleSignin.GoogleSigninActivity;
import com.example.influencer.UI.Login.Model.UsuarioLogin;
import com.example.influencer.UI.SignIn.SignInActivity;
import com.google.android.gms.common.SignInButton;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    TextView sign_inV;
    EditText ET_poner_email;
    EditText ET_poner_contrasena;
    Button B_Button_LogIn;
    SignInButton B_SignInGoogle;
    SweetAlertDialog carga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //para el splash
        androidx.core.splashscreen.SplashScreen splashScreen = androidx.core.splashscreen.SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> false );

        super.onCreate(savedInstanceState);

        //para si el usuario ya esta loggeado no lo enviemos again al layout Login o al layout Signin o al Splash sino al Home
        if(UserPreferences.getInstance(this).isSignedIn()){
            Intent intent_Home= new Intent(LoginActivity.this, Home.class);
            startActivity(intent_Home);
            finish();
        }else{
            setContentView(R.layout.activity_main);}

        //Esto seria para la animacion de fondo de la pantala del LogIn
        ConstraintLayout gradiente_login = findViewById(R.id.gradiente_login);
        AnimationDrawable animacion_login = (AnimationDrawable) gradiente_login.getBackground();

        animacion_login.setEnterFadeDuration(2500);
        animacion_login.setExitFadeDuration(5000);
        animacion_login.start();
        //

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        initLoading();
        initUI();
    }

    private void initLoading(){
        carga = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        carga.getProgressHelper().setBarColor(Color.parseColor("#F57E00"));
        carga.setTitleText(R.string.Loading);
        carga.setCancelable(false);
    }

    private void initUI() {
        initListeners();
        initObservers();
    }

    private void initListeners() {

        //Para hacer el LogIn
        ET_poner_email = findViewById(R.id.poner_email);
        ET_poner_contrasena = findViewById(R.id.poner_contrasena);
        B_Button_LogIn = findViewById(R.id.button_LogIn);

        B_Button_LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email =  ET_poner_email.getText().toString();
                String contrasena = ET_poner_contrasena.getText().toString();
                if(loginViewModel.validatingLogin(email,contrasena)) {
                    loginViewModel.onLoginSelected(new UsuarioLogin(email,contrasena));
                }
            }
        });

        //para el boton de hacer SignIn Con Google
        B_SignInGoogle = findViewById(R.id.buttom_signInGoogle);
        B_SignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginViewModel.onGoogleSignInSelected();
            }
        });

        //para el boton de hacer SignIn manual
        sign_inV = findViewById(R.id.sign_in);
        sign_inV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginViewModel.onSignInSelected();
            }
        });
    }

    private void initObservers() {
        loginViewModel.navigateToHome.observe(this, event -> {
            if (event != null && event.getContentIfNotHandled() != null) {
                goToHome();
            }
        });

        loginViewModel.navigateToGoogleSignIn.observe(this, event -> {
            if (event != null && event.getContentIfNotHandled() != null) {
                goToGoogleSignIn();
            }
        });

        loginViewModel.navigateToSignIn.observe(this, event -> {
            if (event != null && event.getContentIfNotHandled() != null) {
                goToSignIn();
            }
        });

        loginViewModel.Loading.observe(this, new Observer<Event<Boolean>>() {
            @Override
            public void onChanged(Event<Boolean> event) {
                Boolean isLoading = event.getContentIfNotHandled();
                if (isLoading != null) {
                    if (isLoading) {
                        carga.show();
                    } else {
                        carga.dismiss();
                    }
                }
            }
        });

        loginViewModel.getToastMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void goToHome() {
        UserPreferences.getInstance(this).setSignedIn(true);
        Intent intent_LogIn = new Intent(LoginActivity.this, Home.class);
        startActivity(intent_LogIn);
        finish();
    }

    private void  goToGoogleSignIn() {
        Intent intent_GoogleSignIn = new Intent(LoginActivity.this, GoogleSigninActivity.class);
        startActivity(intent_GoogleSignIn);
    }

    private void  goToSignIn() {
        Intent intent_registro = new Intent(LoginActivity.this, SignInActivity.class);
        startActivity(intent_registro);
    }
}
