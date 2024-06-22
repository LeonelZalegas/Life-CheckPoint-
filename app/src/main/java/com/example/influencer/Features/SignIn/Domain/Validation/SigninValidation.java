package com.example.influencer.Features.SignIn.Domain.Validation;

import android.content.res.Resources;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.influencer.R;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SigninValidation  {

    private AwesomeValidation validation;
    private Resources resources;

    @Inject
    public SigninValidation(Resources resources) {
        this.validation = new AwesomeValidation(ValidationStyle.BASIC);
        this.resources = resources;
    }

     public boolean invokeSigninValidation(EditText username, EditText password, EditText email, EditText passwordCheck) {
        validation.addValidation(username,"[a-zA-Z0-9]*", resources.getString(R.string.error_usuario));// para q solo se ponga letras (may y min) y numeros en el Usuario
        validation.addValidation(username,".{6,16}", resources.getString(R.string.error_usuario)); // usuario tiene que tener un minimo de 6 caracteres
        validation.addValidation(email, Patterns.EMAIL_ADDRESS, resources.getString(R.string.error_email));
        validation.addValidation(password,"[a-zA-Z0-9]*", resources.getString(R.string.error_password));// = que lo de arriva pero con la contrasena
        validation.addValidation(password,".{8,}", resources.getString(R.string.error_password));
        validation.addValidation(passwordCheck,password, resources.getString(R.string.error_password_confirmacion)); //controlar si las contrasenas coinciden

         //validation.validate() tendria que dar True si se cumplen todas las condiciones de arriva y false si no
         //ahora...con eso en mente tiene sentido dejar a "validation.validate()" solo nomas, pero por alguna razon ni funciona
         //por eso tuve que hacer esa cosa rara de que si es true devuelva true y si es false devuelva false
         if (validation.validate())
         return true;
         else{
             return false;}
    }


}
