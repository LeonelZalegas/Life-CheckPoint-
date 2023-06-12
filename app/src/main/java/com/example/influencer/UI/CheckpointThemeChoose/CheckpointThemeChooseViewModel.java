package com.example.influencer.UI.CheckpointThemeChoose;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.influencer.Domain.UserCheckpointThemeChooseUseCase;

import java.util.List;

public class CheckpointThemeChooseViewModel extends ViewModel {

    private MutableLiveData<List<CheckpointThemeItem>> rowItems;
    private UserCheckpointThemeChooseUseCase userCheckpointThemeChooseUseCase;
    Context context;

    public CheckpointThemeChooseViewModel(Context context) {
        this.context = context;

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