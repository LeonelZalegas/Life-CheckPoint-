package com.example.influencer.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.influencer.Providers.AuthProvider;
import com.example.influencer.Providers.UsuariosProvider;
import com.example.influencer.R;
import com.example.influencer.models.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Registro extends AppCompatActivity {

    FloatingActionButton volver_hacia_atras;

    EditText ET_poner_usuario;
    EditText ET_poner_email;
    EditText ET_poner_contrasena;
    FloatingActionButton FAB_flecha_avanzar;
    AwesomeValidation Validacion_piola;
    AuthProvider authProvider;
    UsuariosProvider usuariosProvider;

    SweetAlertDialog carga;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //para el cuadro de loading
        carga = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        carga.getProgressHelper().setBarColor(Color.parseColor("#F57E00"));
        carga.setTitleText(R.string.Loading);
        carga.setCancelable(false);

        //para el boton de volver para atras
        volver_hacia_atras = findViewById(R.id.fab_volver_atras);
        volver_hacia_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Validacion de campos
        ET_poner_usuario = findViewById(R.id.poner_usuario);
        ET_poner_email = findViewById(R.id.poner_email);
        ET_poner_contrasena = findViewById(R.id.poner_contrasena);
        FAB_flecha_avanzar = findViewById(R.id.fab_flecha_avanzar);

        authProvider = new AuthProvider();

        Validacion_piola = new AwesomeValidation(ValidationStyle.BASIC);
        Validacion_piola.addValidation(this,R.id.poner_usuario,"[a-zA-Z0-9]*",R.string.error_usuario);// para q solo se ponga letras (may y min) y numeros en el Usuario
        Validacion_piola.addValidation(this,R.id.poner_usuario,".{6,}",R.string.error_usuario); // usuario tiene que tener un minimo de 6 caracteres
        Validacion_piola.addValidation(this,R.id.poner_email,Patterns.EMAIL_ADDRESS,R.string.error_email);
        Validacion_piola.addValidation(this,R.id.poner_contrasena,"[a-zA-Z0-9]*",R.string.error_password);// = que lo de arriva pero con la contrasena
        Validacion_piola.addValidation(this,R.id.poner_contrasena,".{8,}",R.string.error_password);
        Validacion_piola.addValidation(this,R.id.poner_contrasena_again,R.id.poner_contrasena,R.string.error_password_confirmacion); //controlar si las contrasenas coinciden

        FAB_flecha_avanzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarcampos();
            }
        });

    }

    private void validarcampos (){

        String UsuarioNombre = ET_poner_usuario.getText().toString();
        String Email = ET_poner_email.getText().toString();
        String Contrasena = ET_poner_contrasena.getText().toString();

        UsuariosProvider usuariosProvider = new UsuariosProvider();

        if (Validacion_piola.validate()) { //cuando la validacion de todos los campos sea correcta entra aca
            carga.show();
            authProvider.registrar(Email,Contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {     // si la autenticacion del usuario sale todo correcta entra aca
                        //guardamos en Base de datos el usuario autenticado
                        String Id_usuario = authProvider.getuid(); //obtengo el id del usuario que acabo de autencicar correctamente
                        Usuario usuario = new Usuario(Id_usuario, Email, UsuarioNombre);
                            usuariosProvider.crearUsuario(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    carga.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Registro.this, R.string.LogIn_successful, Toast.LENGTH_LONG).show();
                                        Intent intent_LogIn = new Intent(Registro.this,HomeActivity.class);
                                        startActivity(intent_LogIn);
                                        finish();
                                    } else {
                                        Toast.makeText(Registro.this, "Error creating firestore user", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                    }else {
                        carga.dismiss();
                        Toast.makeText(Registro.this, R.string.error_SignIn, Toast.LENGTH_LONG).show(); //para cualquier error relcionado al login de firebase (a su base de datos), saldra este error
                    }
                }
            });

        }
        else{
                Toast.makeText(this, R.string.Re_enter_Fields, Toast.LENGTH_SHORT).show(); // saldra este error cuando algunos de los campos ingresados no cumplen el filtro que pusimos (validacion)
            }
    }
}