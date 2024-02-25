package com.example.influencer.UI.CheckPoint_Tab

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.influencer.Core.Event

@HiltViewModel
class CheckpointTabViewModel @Inject constructor() : ViewModel() {

    private val _navigateToAddingNewCheckpoint = MutableLiveData<Event<Boolean>>()
    val navigateToAddingNewCheckpoint: LiveData<Event<Boolean>> = _navigateToAddingNewCheckpoint

    private val _navigateToAddingNewCheckpointUpdate = MutableLiveData<Event<Boolean>>()
    val navigateToAddingNewCheckpointUpdate: LiveData<Event<Boolean>> = _navigateToAddingNewCheckpointUpdate

    fun onAddingNewCheckpointSelected() {
        _navigateToAddingNewCheckpoint.value = Event(true)
    }

    fun onAddingNewCheckpointUpdateSelected() {
        _navigateToAddingNewCheckpointUpdate.value = Event(true)
    }
}