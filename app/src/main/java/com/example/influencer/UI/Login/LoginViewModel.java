package com.example.influencer.UI.Login;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.influencer.Core.Event;
import com.example.influencer.Domain.LoginUseCase;
import com.example.influencer.R;
import com.example.influencer.UI.Login.Model.UsuarioLogin;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginViewModel extends ViewModel implements LoginListener {
    private final LoginUseCase loginUseCase;
    SweetAlertDialog carga;
    Context context;

    private final MutableLiveData<Event<Boolean>> _navigateToSignIn = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToSignIn = _navigateToSignIn;

    private final MutableLiveData<Event<Boolean>> _navigateToGoogleSignIn = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToGoogleSignIn = _navigateToGoogleSignIn;

    private final MutableLiveData<Event<Boolean>> _navigateToOnBoarding = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToOnBoarding = _navigateToOnBoarding;

    public LoginViewModel(LoginUseCase loginUseCase, Context context) {
        this.loginUseCase = loginUseCase;
        this.context = context;

        //para el cuadro de loading
        carga = new SweetAlertDialog(context,SweetAlertDialog.PROGRESS_TYPE);
        carga.getProgressHelper().setBarColor(Color.parseColor("#F57E00"));
        carga.setTitleText(R.string.Loading);
        carga.setCancelable(false);
    }

    public void onLoginSelected(UsuarioLogin usuarioLogin) {
        loginUseCase.invoke(usuarioLogin, this);
    }


    @Override
    public void LoginSuccess() {
        _navigateToOnBoarding.postValue(new Event<>(true));
        carga.dismiss();
    }

    @Override
    public void LoginError() {
        carga.dismiss();
        Toast.makeText(context, R.string.error_LogIn, Toast.LENGTH_LONG).show();
    }

    public void onSignInSelected() {
        _navigateToSignIn.setValue(new Event<>(true));
    }

    public void onGoogleSignInSelected() {
        _navigateToGoogleSignIn.setValue(new Event<>(true));
    }


    public boolean validatingLogin(String email, String contrasena){
        carga.show();
        if( (email != null && !email.trim().isEmpty()) && (contrasena != null && !contrasena.trim().isEmpty()) ) {  //esto es para controlar que no se ingresen nulls (no se cargue ningun valor) y tampoco espacios
            return true;
        }else {
            carga.dismiss();
            Toast.makeText(context, R.string.empty_fields_LogIn, Toast.LENGTH_LONG).show();
            return false;
        }
    }

}
