package com.example.influencer.Domain;

import com.example.influencer.Data.Network.AuthenticationService;
import com.example.influencer.Data.Network.UserService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class CheckIfUserExistsUseCase {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public CheckIfUserExistsUseCase(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    public Task<DocumentSnapshot> execute() {
        String userId = authenticationService.getUid();
        return userService.getUsuario(userId);
    }
}
