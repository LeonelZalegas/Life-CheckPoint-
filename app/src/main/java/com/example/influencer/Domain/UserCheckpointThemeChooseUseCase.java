package com.example.influencer.Domain;

import com.example.influencer.Data.Network.AuthenticationService;
import com.example.influencer.Data.Network.FirebaseClient;
import com.example.influencer.Data.Network.UserService;
import com.example.influencer.Data.Repositories.CheckpointChooseRowsRepo;
import com.example.influencer.R;
import com.example.influencer.UI.CheckpointThemeChoose.CheckpointThemeItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

//esta clase sirve para obtener tanto los rows que son fijos(estaticos) como los rows personales que ha guardado cada usuario (en firestore)

public class UserCheckpointThemeChooseUseCase {

    private UserService userService;
    private CheckpointChooseRowsRepo checkpointChooseRowsRepo;

    public UserCheckpointThemeChooseUseCase() {
        FirebaseClient firebaseClient = FirebaseClient.getInstance();
        this.userService = new UserService(firebaseClient);
        this.checkpointChooseRowsRepo = new CheckpointChooseRowsRepo(userService);
    }

    public Task<Void> addCheckpointTheme(String checkpointThemeName){
        String userId = AuthenticationService.getInstance().getuid();
        if (userId != null){
            return userService.addCheckpointThemeToUser(userId,checkpointThemeName);
        }
        return null;
    }

    public Flowable<List<CheckpointThemeItem>> execute() {
        String userId = AuthenticationService.getInstance().getuid();
        if (userId != null){
            return checkpointChooseRowsRepo.getAllCheckpointThemes(userId);
        } else {
            return null;
        }
    }
}
