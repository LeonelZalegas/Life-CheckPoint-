package com.example.influencer.Features.SignIn.UI.AppSignIn;

import android.content.res.Resources;
import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin;
import com.example.influencer.Core.Utils.Event;
import com.example.influencer.Core.Utils.SingleLiveEvent;
import com.example.influencer.Features.SignIn.Domain.CreateAccountUseCase;
import com.example.influencer.Features.SignIn.Domain.Validation.SigninValidation;
import com.example.influencer.R;
import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SignInViewModel extends ViewModel implements CreateAccountListener {
    private final CreateAccountUseCase createAccountUseCase;
    private final Resources resources;
    private final SigninValidation signinValidation;

    private final SingleLiveEvent<String> ToastMessage = new SingleLiveEvent<>();

    private final MutableLiveData<Event<Boolean>> _Loading = new MutableLiveData<>();
    public LiveData<Event<Boolean>> Loading = _Loading;

    private final MutableLiveData<Event<Boolean>> _backToLogin = new MutableLiveData<>();
    public LiveData<Event<Boolean>> backToLogin = _backToLogin;

    private final MutableLiveData<Event<Boolean>> _navigateToOnBoarding = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToOnBoarding = _navigateToOnBoarding;

    @Inject
    public SignInViewModel(Resources resources,CreateAccountUseCase createAccountUseCase, SigninValidation signinValidation) {
        this.createAccountUseCase = createAccountUseCase;
        this.resources = resources;
        this.signinValidation = signinValidation;
    }

    public void onSignInSelected(UsuarioSignin usuarioSignin) {
        _Loading.setValue(new Event<>(true));
        createAccountUseCase.invoke(usuarioSignin, this);
    }


    @Override
    public void onCreateAccountSuccess() {
        ToastMessage.setValue(resources.getString(R.string.LogIn_successful));
        _navigateToOnBoarding.postValue(new Event<>(true));
        _Loading.setValue(new Event<>(false));
    }

    @Override
    public void onCreateAccountError() {
        ToastMessage.setValue(resources.getString(R.string.error_SignIn_Firestore));
        _Loading.setValue(new Event<>(false));
    }

    public void backToLoginSelected() {
        _backToLogin.setValue(new Event<>(true));
    }

    public boolean validatingSignIn(EditText username, EditText password, EditText email, EditText passwordCheck){
        return signinValidation.invokeSigninValidation(username,password,email,passwordCheck);
    }

    public SingleLiveEvent<String> getToastMessage() {
        return ToastMessage;
    }
}
