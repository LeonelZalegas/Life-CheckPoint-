package com.example.influencer.UI.CheckPoint_Tab;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.influencer.Core.Event;
import com.example.influencer.UI.Login.Model.UsuarioLogin;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CheckpointTabViewModel extends ViewModel {

    @Inject
    public CheckpointTabViewModel(){
        //no dependencies needed
    }

    private final MutableLiveData<Event<Boolean>> _navigateToAddingNewCheckpoint = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToAddingNewCheckpoint = _navigateToAddingNewCheckpoint;

    private final MutableLiveData<Event<Boolean>> _navigateToAddingNewCheckpointUpdate = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToAddingNewCheckpointUpdate = _navigateToAddingNewCheckpointUpdate;

    public void onAddingNewCheckpointSelected() {
        _navigateToAddingNewCheckpoint.setValue(new Event<>(true));
    }

    public void onAddingNewCheckpointUpdateSelected() {
        _navigateToAddingNewCheckpointUpdate.setValue(new Event<>(true));
    }

}
