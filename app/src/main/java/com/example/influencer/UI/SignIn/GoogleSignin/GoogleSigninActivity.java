package com.example.influencer.UI.SignIn.GoogleSignin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.influencer.Core.Event;
import com.example.influencer.Data.Preferences.UserPreferences;
import com.example.influencer.UI.Home;
import com.example.influencer.UI.OnBoarding___CountryAndDateSelector.OnBoarding.OnBoardingActivity;
import com.example.influencer.R;
import com.example.influencer.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GoogleSigninActivity extends AppCompatActivity {

    private GoogleSigninViewModel googleSigninViewModel;
    SweetAlertDialog carga;
    private ActivityMainBinding binding;
    @Inject
    UserPreferences userPreferences;
    @Inject
    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        googleSigninViewModel = new ViewModelProvider(this).get(GoogleSigninViewModel.class);
        initLoading();
        initBasicObservers();

        ActivityResultLauncher<Intent> resultadoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d("error", "firebaseAuthWithGoogle:" + account.getId());

                            // Get the profile picture URL
                            String profilePictureUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null;
                            googleSigninViewModel.handleGoogleSignInResult(account.getIdToken(),profilePictureUrl);

                        } catch (ApiException e) {
                            Log.w("error", "Google sign in failed", e);
                            Toast.makeText(this, R.string.error_SignIn_Firestore, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        resultadoLauncher.launch(mGoogleSignInClient.getSignInIntent());

        googleSigninViewModel.getUserExists().observe(this, userExists -> {
            if (userExists) {
                goToHome();
            }
        });

        googleSigninViewModel.getUserGetsCreated().observe(this, userGetsCreated -> {
            if (userGetsCreated) {
                goToOnBoarding();
            }
        });
    }

    private void initBasicObservers() {
        googleSigninViewModel.Loading.observe(this, new Observer<Event<Boolean>>() {
            @Override
            public void onChanged(Event<Boolean> event) {
                Boolean isLoading = event.getContentIfNotHandled();
                if (isLoading != null) {
                    if (isLoading) {
                        carga.show();
                    } else {
                        carga.dismiss();
                    }
                }
            }
        });

        googleSigninViewModel.getToastMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initLoading(){
        carga = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        carga.getProgressHelper().setBarColor(Color.parseColor("#F57E00"));
        carga.setTitleText(R.string.Loading);
        carga.setCancelable(false);
    }

    private void goToOnBoarding(){
        userPreferences.setSignedIn(true);
        Intent intent_LogIn = new Intent(GoogleSigninActivity.this, OnBoardingActivity.class);
        startActivity(intent_LogIn);
        finish();
    }

    private void goToHome(){
        userPreferences.setSignedIn(true);
        Intent intent_LogIn = new Intent(GoogleSigninActivity.this, Home.class);
        startActivity(intent_LogIn);
        finish();
    }
}
