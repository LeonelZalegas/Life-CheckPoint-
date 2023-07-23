package com.example.influencer.UI.SignIn;

import android.content.Context;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.influencer.Core.Event;
import com.example.influencer.Domain.CreateAccountUseCase;
import com.example.influencer.Domain.Validations.SigninValidation;
import com.example.influencer.R;
import com.example.influencer.UI.SignIn.Model.UsuarioSignin;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignInViewModel extends ViewModel implements CreateAccountListener {
    private final CreateAccountUseCase createAccountUseCase;
    SweetAlertDialog carga;
    Context context; //para asi poder usar los Toast

    private final MutableLiveData<Event<Boolean>> _backToLogin = new MutableLiveData<>();
    public LiveData<Event<Boolean>> backToLogin = _backToLogin;

    private final MutableLiveData<Event<Boolean>> _navigateToOnBoarding = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToOnBoarding = _navigateToOnBoarding;

    public SignInViewModel(CreateAccountUseCase createAccountUseCase, Context context) {
        this.createAccountUseCase = createAccountUseCase;
        this.context = context;

        //para el cuadro de loading
        carga = new SweetAlertDialog(context,SweetAlertDialog.PROGRESS_TYPE);
        carga.getProgressHelper().setBarColor(Color.parseColor("#F57E00"));
        carga.setTitleText(R.string.Loading);
        carga.setCancelable(false);
    }

    public void onSignInSelected(UsuarioSignin usuarioSignin) {
        carga.show();
        createAccountUseCase.invoke(usuarioSignin, this, context);
    }


    @Override
    public void onCreateAccountSuccess() {
        _navigateToOnBoarding.postValue(new Event<>(true));
        carga.dismiss();
    }

    @Override
    public void onCreateAccountError() {
        carga.dismiss();
    }

    public void backToLoginSelected() {
        _backToLogin.setValue(new Event<>(true));
    }

    public boolean validatingSignIn(AppCompatActivity signinContext){
        return SigninValidation.invoke(signinContext);
    }
}
