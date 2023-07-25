package com.example.influencer.UI.Login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.influencer.Core.Event;
import com.example.influencer.Core.MyApp;
import com.example.influencer.Core.SingleLiveEvent;
import com.example.influencer.Data.Network.AuthenticationService;
import com.example.influencer.Domain.LoginUseCase;
import com.example.influencer.R;
import com.example.influencer.UI.Login.Model.UsuarioLogin;


public class LoginViewModel extends ViewModel implements LoginListener {
    private final LoginUseCase loginUseCase;

    private final SingleLiveEvent<String> ToastMessage = new SingleLiveEvent<>();

    private final MutableLiveData<Event<Boolean>> _Loading = new MutableLiveData<>();
    public LiveData<Event<Boolean>> Loading = _Loading;

    private final MutableLiveData<Event<Boolean>> _navigateToSignIn = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToSignIn = _navigateToSignIn;

    private final MutableLiveData<Event<Boolean>> _navigateToGoogleSignIn = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToGoogleSignIn = _navigateToGoogleSignIn;

    private final MutableLiveData<Event<Boolean>> _navigateToHome = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToHome = _navigateToHome;

    public LoginViewModel() {
        loginUseCase = new LoginUseCase(AuthenticationService.getInstance());
    }

    public void onLoginSelected(UsuarioLogin usuarioLogin) {
        loginUseCase.invoke(usuarioLogin, this);
    }


    @Override
    public void LoginSuccess() {
        _navigateToHome.postValue(new Event<>(true));
        _Loading.setValue(new Event<>(false));
    }

    @Override
    public void LoginError() {
        _Loading.setValue(new Event<>(false));
        ToastMessage.setValue(MyApp.getInstance().getAString(R.string.error_LogIn));
    }

    public void onSignInSelected() {
        _navigateToSignIn.setValue(new Event<>(true));
    }

    public void onGoogleSignInSelected() {
        _navigateToGoogleSignIn.setValue(new Event<>(true));
    }


    public boolean validatingLogin(String email, String contrasena){
        _Loading.setValue(new Event<>(true));
        if( (email != null && !email.trim().isEmpty()) && (contrasena != null && !contrasena.trim().isEmpty()) ) {  //esto es para controlar que no se ingresen nulls (no se cargue ningun valor) y tampoco espacios
            return true;
        }else {
            _Loading.setValue(new Event<>(false));
            ToastMessage.setValue(MyApp.getInstance().getAString(R.string.empty_fields_LogIn));
            return false;
        }
    }

    public SingleLiveEvent<String> getToastMessage() {
        return ToastMessage;
    }
}
