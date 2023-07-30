package com.example.influencer.Domain;

import com.example.influencer.Data.Network.AuthenticationService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class FirebaseAuthWithGoogleUseCase {

    private final AuthenticationService authenticationService;

    public FirebaseAuthWithGoogleUseCase(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public Task<AuthResult> execute(String idToken) {
        return authenticationService.googleSignin(idToken);
    }
}