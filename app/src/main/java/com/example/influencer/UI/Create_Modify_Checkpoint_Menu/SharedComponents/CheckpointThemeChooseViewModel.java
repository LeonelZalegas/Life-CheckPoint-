package com.example.influencer.UI.Create_Modify_Checkpoint_Menu.SharedComponents;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;

import com.example.influencer.Core.MyApp;
import com.example.influencer.Core.SingleLiveEvent;
import com.example.influencer.Domain.UserCheckpointThemeChooseUseCase;
import com.example.influencer.Domain.Validations.NewCheckpointThemeValidation;
import com.example.influencer.R;
import com.example.influencer.UI.Create_Modify_Checkpoint_Menu.SharedComponents.Model.CheckpointThemeItem;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class CheckpointThemeChooseViewModel extends ViewModel {

    private UserCheckpointThemeChooseUseCase userCheckpointThemeChooseUseCase;

    //https://www.notion.so/Activity-seleccionar-categoria-nuevo-checkpoint-update-checkpoint-2fe38f46f27f4e6f93752aa178796773?pvs=4#4fdf8783463a4983af9d292ab5ebf3ee
    private SingleLiveEvent<String> toastMessage = new SingleLiveEvent<>();

    public CheckpointThemeChooseViewModel(@NonNull Application application) {
        userCheckpointThemeChooseUseCase = new UserCheckpointThemeChooseUseCase();
    }

    public LiveData<List<CheckpointThemeItem>> getUserCheckpointsThemes() {
        return LiveDataReactiveStreams.fromPublisher(userCheckpointThemeChooseUseCase.getUserCheckpointsThemes());
    }

    public boolean validatingNewThemeCheckpoint(TextInputEditText editText){
        return NewCheckpointThemeValidation.invoke(editText);
    }

    //https://www.notion.so/Life-Checkpoint-37dccb9dbb464169b4c9a42460f50f40?pvs=4#c843682676ae4d298b72adbd31b745ac
    public void addCheckpointThemeName(String checkpointThemeName) {
        Task<Void> task = userCheckpointThemeChooseUseCase.addCheckpointTheme(checkpointThemeName);
        if (task != null) {
            task.addOnFailureListener(e -> toastMessage.setValue(MyApp.getInstance().getAString(R.string.FireStore_Error)));
        }
    }

    public SingleLiveEvent<String> getToastMessage() {
        return toastMessage;
    }
}