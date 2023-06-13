package com.example.influencer.UI.CheckpointThemeChoose;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.influencer.Domain.UserCheckpointThemeChooseUseCase;

import java.util.List;

public class CheckpointThemeChooseViewModel extends AndroidViewModel {

    private MutableLiveData<List<CheckpointThemeItem>> rowItems;
    private UserCheckpointThemeChooseUseCase userCheckpointThemeChooseUseCase;
    Context context;

    public CheckpointThemeChooseViewModel(@NonNull Application application) {
        super(application);
        this.context = application;

        rowItems = new MutableLiveData<>();
        userCheckpointThemeChooseUseCase = new UserCheckpointThemeChooseUseCase(context);

        //obtenemos la lista actualizada de checkpoints(tanto fijas como la del usuario propio) y en tiempo real constantemente la ponemos en rowitems
        List<CheckpointThemeItem> updatedRows = userCheckpointThemeChooseUseCase.fetchStaticRows(); //aca habria q despues cambiar y poner el metodo execute
        rowItems.setValue(updatedRows);
    }

    public LiveData<List<CheckpointThemeItem>> getUserCheckpointsThemes(){
        return rowItems;
    }
}