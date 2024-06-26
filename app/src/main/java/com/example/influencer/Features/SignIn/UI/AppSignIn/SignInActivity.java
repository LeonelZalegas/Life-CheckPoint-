package com.example.influencer.Features.SignIn.UI.AppSignIn;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.influencer.Core.Data.Preferences.UserPreferences;
import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin;
import com.example.influencer.Core.Utils.Event;
import com.example.influencer.Core.Data.Preferences.UserPreferences;
import com.example.influencer.Features.OnBoarding___CountryAndDateSelector.UI.OnBoarding.OnBoardingActivity;
import com.example.influencer.R;
import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin;
import com.example.influencer.databinding.ActivityRegistroBinding;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignInActivity extends AppCompatActivity {

    private SignInViewModel signInViewModel;
    @Inject
    UserPreferences userPreferences;
    private ActivityRegistroBinding binding;
    SweetAlertDialog carga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        binding.fabVolverAtras.setOnClickListener(view -> signInViewModel.backToLoginSelected());

        //boton para seguir avanzando (ir a home)
        binding.fabFlechaAvanzar.setOnClickListener(view -> {

            EditText username =  binding.ponerUsuario;
            EditText password = binding.ponerContrasena;
            EditText email = binding.ponerEmail;
            EditText passwordCheck = binding.ponerContrasenaAgain;

            if (signInViewModel.validatingSignIn(username,password,email,passwordCheck)) {
                signInViewModel.onSignInSelected(new UsuarioSignin(
                        email.getText().toString(),
                        username.getText().toString(),
                        password.getText().toString()));
            }else{
                Toast.makeText(this, R.string.Re_enter_Fields, Toast.LENGTH_SHORT).show();
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
        userPreferences.setSignedInSync(true);
        Intent intent_LogIn = new Intent(SignInActivity.this, OnBoardingActivity.class);
        startActivity(intent_LogIn);
        ActivityCompat.finishAffinity(this); //en vez de poner finish() nomas utilizamos este metodo par asi tambien finalizar el activity de Login (antes solo se cerraba el Signin, por ende podias ir de Home a Login de nuevo si volvias para atras)
    }

    private void goTobackToLogin() {
        finish();
    }
}
