package com.example.influencer.UI.GoogleSignin;

import android.content.Intent;

import androidx.lifecycle.ViewModel;

import com.example.influencer.Domain.GoogleSigninUseCase;

public class GoogleSigninViewModel extends ViewModel {

    private GoogleSigninUseCase googleSigninUseCase;
    private GoogleSigninListener listener;

    public GoogleSigninViewModel(GoogleSigninUseCase googleSigninUseCase, GoogleSigninListener listener) {
        this.googleSigninUseCase = googleSigninUseCase;
        this.listener = listener;
    }

    public void signInWithGoogle(Intent signInIntent) {
        googleSigninUseCase.signInWithGoogle(signInIntent, new com.example.influencer.UI.GoogleSignin.Callback() {
            @Override
            public void onSuccess() {
                listener.onUserAuthenticated();
            }

            @Override
            public void onError() {
                listener.onError();
            }
        });
    }

}
