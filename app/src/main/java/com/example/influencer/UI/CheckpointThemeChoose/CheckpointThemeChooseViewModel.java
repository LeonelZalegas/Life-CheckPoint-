package com.example.influencer.UI.CheckpointThemeChoose;

import android.app.Application;
import android.content.Context;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.influencer.Core.SingleLiveEvent;
import com.example.influencer.Domain.UserCheckpointThemeChooseUseCase;
import com.example.influencer.R;
import com.example.influencer.databinding.AlertDialogAddCategoryBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class CheckpointThemeChooseViewModel extends AndroidViewModel {

    private UserCheckpointThemeChooseUseCase userCheckpointThemeChooseUseCase;
    Context context;
    private SingleLiveEvent<String> toastMessage = new SingleLiveEvent<>();

    public CheckpointThemeChooseViewModel(@NonNull Application application) {
        super(application);
        this.context = application;

        userCheckpointThemeChooseUseCase = new UserCheckpointThemeChooseUseCase();
    }

    public LiveData<List<CheckpointThemeItem>> getUserCheckpointsThemes() {
        return LiveDataReactiveStreams.fromPublisher(userCheckpointThemeChooseUseCase.execute());
    }

    public boolean validatingNewThemeCheckpoint(TextInputEditText editText){
        return NewCheckpointThemeValidation.invoke(editText);
    }

    //https://www.notion.so/Life-Checkpoint-37dccb9dbb464169b4c9a42460f50f40?pvs=4#c843682676ae4d298b72adbd31b745ac
    public void addCheckpointThemeName(String checkpointThemeName) {
        Task<Void> task = userCheckpointThemeChooseUseCase.addCheckpointTheme(checkpointThemeName);
        if (task != null) {
            task.addOnSuccessListener(aVoid -> toastMessage.setValue("Category successfully added!"))
                    .addOnFailureListener(e -> toastMessage.setValue(context.getString(R.string.FireStore_Error)));
        }
    }

    public SingleLiveEvent<String> getToastMessage() {
        return toastMessage;
    }

}