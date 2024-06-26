package com.example.influencer.Features.SignIn.Domain;

import com.example.influencer.Core.Data.Network.AuthenticationService;
import com.example.influencer.Core.Data.Network.AuthenticationService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import javax.inject.Inject;

public class FirebaseAuthWithGoogleUseCase {

    private final AuthenticationService authenticationService;

    @Inject
    public FirebaseAuthWithGoogleUseCase(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public Task<AuthResult> execute(String idToken) {
        return authenticationService.googleSignin(idToken);
    }
}