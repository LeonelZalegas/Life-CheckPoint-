package com.example.influencer.Domain.Validations;

import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.influencer.R;

public class SigninValidation  {

    static public boolean invoke(AppCompatActivity context) {
        AwesomeValidation Validacion_piola;
        Validacion_piola = new AwesomeValidation(ValidationStyle.BASIC);
        Validacion_piola.addValidation(context,R.id.poner_usuario,"[a-zA-Z0-9]*",R.string.error_usuario);// para q solo se ponga letras (may y min) y numeros en el Usuario
        Validacion_piola.addValidation(context,R.id.poner_usuario,".{6,}",R.string.error_usuario); // usuario tiene que tener un minimo de 6 caracteres
        Validacion_piola.addValidation(context,R.id.poner_email, Patterns.EMAIL_ADDRESS,R.string.error_email);
        Validacion_piola.addValidation(context,R.id.poner_contrasena,"[a-zA-Z0-9]*",R.string.error_password);// = que lo de arriva pero con la contrasena
        Validacion_piola.addValidation(context,R.id.poner_contrasena,".{8,}",R.string.error_password);
        Validacion_piola.addValidation(context,R.id.poner_contrasena_again,R.id.poner_contrasena,R.string.error_password_confirmacion); //controlar si las contrasenas coinciden

        if (Validacion_piola.validate()) {
            return true;
        } else{
            Toast.makeText(context, R.string.Re_enter_Fields, Toast.LENGTH_SHORT).show(); // saldra este error cuando algunos de los campos ingresados no cumplen el filtro que pusimos (validacion)
            return  false;
        }
    }
}
