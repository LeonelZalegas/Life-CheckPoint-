package com.example.influencer.Features.SignIn.Domain;

import com.example.influencer.Core.Data.Network.AuthenticationService;
import com.example.influencer.Core.Data.Network.UserService;
import com.example.influencer.Core.Data.Network.AuthenticationService;
import com.example.influencer.Core.Data.Network.UserService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import javax.inject.Inject;

public class CheckIfUserExistsUseCase {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Inject
    public CheckIfUserExistsUseCase(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    public Task<DocumentSnapshot> execute() {
        String userId = authenticationService.getUid();
        return userService.getUsuario(userId);
    }
}