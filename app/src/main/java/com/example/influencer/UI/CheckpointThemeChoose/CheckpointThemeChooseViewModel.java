package com.example.influencer.UI.CheckpointThemeChoose;

import android.app.Application;
import android.content.Context;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.influencer.Domain.UserCheckpointThemeChooseUseCase;
import com.example.influencer.databinding.AlertDialogAddCategoryBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class CheckpointThemeChooseViewModel extends AndroidViewModel {

    private UserCheckpointThemeChooseUseCase userCheckpointThemeChooseUseCase;
    Context context;

    public CheckpointThemeChooseViewModel(@NonNull Application application) {
        super(application);
        this.context = application;

        userCheckpointThemeChooseUseCase = new UserCheckpointThemeChooseUseCase(context);
    }

    public LiveData<List<CheckpointThemeItem>> getUserCheckpointsThemes(){
        return userCheckpointThemeChooseUseCase.execute();
    }

    public boolean validatingNewThemeCheckpoint(TextInputEditText editText){
        return NewCheckpointThemeValidation.invoke(editText);
    }

    //https://www.notion.so/Life-Checkpoint-37dccb9dbb464169b4c9a42460f50f40?pvs=4#c843682676ae4d298b72adbd31b745ac
    public void addCheckpointThemeName(String checkpointThemeName) {
        userCheckpointThemeChooseUseCase.addCheckpointTheme(checkpointThemeName);
    }
}