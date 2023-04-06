package com.example.influencer.UI.Login;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.influencer.Activities.OnBoardingActivity;
import com.example.influencer.Data.Network.AuthenticationService;
import com.example.influencer.Domain.LoginUseCase;
import com.example.influencer.R;
import com.example.influencer.UI.GoogleSignin.GoogleSigninActivity;
import com.example.influencer.UI.Login.Model.UsuarioLogin;
import com.example.influencer.UI.SignIn.SignInActivity;
import com.google.android.gms.common.SignInButton;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    TextView sign_inV;
    EditText ET_poner_email;
    EditText ET_poner_contrasena;
    Button B_Button_LogIn;
    SignInButton B_SignInGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //para el splash
        androidx.core.splashscreen.SplashScreen splashScreen = androidx.core.splashscreen.SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> false );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Esto seria para la animacion de fondo de la pantala del LogIn
        ConstraintLayout gradiente_login = findViewById(R.id.gradiente_login);
        AnimationDrawable animacion_login = (AnimationDrawable) gradiente_login.getBackground();

        animacion_login.setEnterFadeDuration(2500);
        animacion_login.setExitFadeDuration(5000);
        animacion_login.start();
        //

        LoginUseCase loginUseCase = new LoginUseCase(AuthenticationService.getInstance());
        loginViewModel = new LoginViewModel(loginUseCase, this);
        initUI();
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
                goTonavigateToHome();
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
    }

    private void goTonavigateToHome() {
        Intent intent_LogIn = new Intent(LoginActivity.this, OnBoardingActivity.class);
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
