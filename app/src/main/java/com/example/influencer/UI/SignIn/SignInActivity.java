package com.example.influencer.UI.SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.influencer.Activities.HomeActivity;
import com.example.influencer.Data.Network.AuthenticationService;
import com.example.influencer.Data.Network.FirebaseClient;
import com.example.influencer.Data.Network.UserService;
import com.example.influencer.Domain.CreateAccountUseCase;
import com.example.influencer.R;
import com.example.influencer.UI.SignIn.Model.UsuarioSignin;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SignInActivity extends AppCompatActivity {
    private SignInViewModel signInViewModel;
    FloatingActionButton FAB_flecha_avanzar;
    FloatingActionButton volver_hacia_atras;
    EditText ET_poner_usuario;
    EditText ET_poner_email;
    EditText ET_poner_contrasena;
    AppCompatActivity ActivityContext = this; //para poder hacer la validacion del Signin (y no volverla un Activity)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        CreateAccountUseCase createAccountUseCase = new CreateAccountUseCase(AuthenticationService.getInstance(), new UserService(FirebaseClient.getInstance()));
        signInViewModel = new SignInViewModel(createAccountUseCase, this);
        initUI();
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
                    signInViewModel.finishvalidatingSignIn();
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
        signInViewModel.navigateToHome.observe(this, event -> {
            if (event != null && event.getContentIfNotHandled() != null) {
                goTonavigateToHome();
            }
        });

        signInViewModel.backToLogin.observe(this, event -> {
            if (event != null && event.getContentIfNotHandled() != null) {
                goTobackToLogin();
            }
        });

    }

    private void goTonavigateToHome() {
        Intent intent_LogIn = new Intent(SignInActivity.this, HomeActivity.class);
        startActivity(intent_LogIn);
        finish();
    }

    private void goTobackToLogin() {
        finish();
    }
}
