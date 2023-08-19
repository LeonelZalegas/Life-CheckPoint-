package com.example.influencer.Domain;

import com.example.influencer.Data.Network.AuthenticationService;
import com.example.influencer.Data.Network.UserService;
import com.example.influencer.Data.Repositories.CheckpointChooseRowsRepo;
import com.example.influencer.UI.Create_Modify_Checkpoint_Menu.SharedComponents.Model.CheckpointThemeItem;
import com.google.android.gms.tasks.Task;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;

//esta clase sirve unicamente para logica de negocios

public class UserCheckpointThemeChooseUseCase {

    private UserService userService;
    private CheckpointChooseRowsRepo checkpointChooseRowsRepo;
    private final AuthenticationService authenticationService;

    @Inject
    public UserCheckpointThemeChooseUseCase(CheckpointChooseRowsRepo checkpointChooseRowsRepo,
                                            UserService userService,
                                            AuthenticationService authenticationService) {
        this.checkpointChooseRowsRepo = checkpointChooseRowsRepo;
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    public Task<Void> addCheckpointTheme(String checkpointThemeName){
        String userId = authenticationService.getUid();
        if (userId != null){
            return userService.addCheckpointThemeToUser(userId,checkpointThemeName);
        }
        return null;
    }

    public Flowable<List<CheckpointThemeItem>> getUserCheckpointsThemes() {
        String userId = authenticationService.getUid();
        if (userId != null){
            return checkpointChooseRowsRepo.getAllCheckpointThemes(userId);
        } else {
            return null;
        }
    }
}
