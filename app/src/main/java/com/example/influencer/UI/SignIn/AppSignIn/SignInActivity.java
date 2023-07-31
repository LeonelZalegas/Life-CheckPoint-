package com.example.influencer.UI.SignIn.AppSignIn;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.influencer.Core.Event;
import com.example.influencer.Data.Preferences.UserPreferences;
import com.example.influencer.UI.OnBoarding.OnBoardingActivity;
import com.example.influencer.R;
import com.example.influencer.UI.SignIn.Model.UsuarioSignin;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignInActivity extends AppCompatActivity {
    private SignInViewModel signInViewModel;
    FloatingActionButton FAB_flecha_avanzar;
    FloatingActionButton volver_hacia_atras;
    EditText ET_poner_usuario;
    EditText ET_poner_email;
    EditText ET_poner_contrasena;
    AppCompatActivity ActivityContext = this; //para poder hacer la validacion del Signin (y no volverla un Activity)
    SweetAlertDialog carga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);
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

        //para el boton de volver para atras
        volver_hacia_atras = findViewById(R.id.fab_volver_atras);
        volver_hacia_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInViewModel.backToLoginSelected();
            }
        });

        //boton para seguir avanzando (ir a home)
        ET_poner_usuario = findViewById(R.id.poner_usuario);
        ET_poner_email = findViewById(R.id.poner_email);
        ET_poner_contrasena = findViewById(R.id.poner_contrasena);
        FAB_flecha_avanzar = findViewById(R.id.fab_flecha_avanzar);
        FAB_flecha_avanzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (signInViewModel.validatingSignIn(ActivityContext)) {
                    signInViewModel.onSignInSelected(new UsuarioSignin(
                            ET_poner_email.getText().toString(),
                            ET_poner_usuario.getText().toString(),
                            ET_poner_contrasena.getText().toString()
                    ));
                }
            }
        });
    }

    private void initObservers() {
        signInViewModel.navigateToOnBoarding.observe(this, event -> {
            if (event != null && event.getContentIfNotHandled() != null) {
                goTonavigateToOnBoarding();
            }
        });

        signInViewModel.backToLogin.observe(this, event -> {
            if (event != null && event.getContentIfNotHandled() != null) {
                goTobackToLogin();
            }
        });

        signInViewModel.Loading.observe(this, new Observer<Event<Boolean>>() {
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

        signInViewModel.getToastMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void goTonavigateToOnBoarding() {
        UserPreferences.getInstance(this).setSignedIn(true);
        Intent intent_LogIn = new Intent(SignInActivity.this, OnBoardingActivity.class);
        startActivity(intent_LogIn);
        ActivityCompat.finishAffinity(this); //en vez de poner finish() nomas utilizamos este metodo par asi tambien finalizar el activity de Login (antes solo se cerraba el Signin, por ende podias ir de Home a Login de nuevo si volvias para atras)
    }

    private void goTobackToLogin() {
        finish();
    }
}
