package com.example.influencer.UI.GoogleSignin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.influencer.Activities.OnBoardingActivity;
import com.example.influencer.Data.Network.AuthenticationService;
import com.example.influencer.Data.Network.FirebaseClient;
import com.example.influencer.Data.Network.UserService;
import com.example.influencer.Domain.GoogleSigninUseCase;
import com.example.influencer.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class GoogleSigninActivity extends AppCompatActivity implements GoogleSigninListener {

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSigninViewModel googleSigninViewModel;
    private GoogleSigninUseCase googleSigninUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSigninUseCase = new GoogleSigninUseCase(AuthenticationService.getInstance(), new UserService(FirebaseClient.getInstance()),this,this);
        googleSigninViewModel = new GoogleSigninViewModel(googleSigninUseCase, this);

        googleSigninViewModel.signInWithGoogle(new Intent(mGoogleSignInClient.getSignInIntent()));

    }

    @Override
    public void onUserAuthenticated() {
        Intent intent_LogIn = new Intent(GoogleSigninActivity.this, OnBoardingActivity.class);
        startActivity(intent_LogIn);
        finish();
    }

    @Override
    public void onError() {
        Toast.makeText(GoogleSigninActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
        finish();
    }
}
