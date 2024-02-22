package com.example.influencer.UI.Upload_New_Update_Checkpoint.Fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.influencer.Domain.CreateCheckPointUpdateUseCase
import com.example.influencer.Domain.GetNextUpdateNumberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class Checkpoint_TextBox_Viewmodel @Inject constructor(
    private val createCheckPointUpdateUseCase: CreateCheckPointUpdateUseCase,
    private val getNextUpdateNumberUseCase: GetNextUpdateNumberUseCase
) : ViewModel() {

    private val _nextUpdateNumber = MutableLiveData<Int?>()
    val nextUpdateNumber = _nextUpdateNumber
    private val _updateSaveSuccessLiveData = MutableLiveData<Boolean>()
    val updateSaveSuccessLiveData: LiveData<Boolean> = _updateSaveSuccessLiveData

    fun getNextUpdateNumber(selectedCategory: String){
        viewModelScope.launch {
            _nextUpdateNumber.value = getNextUpdateNumberUseCase(selectedCategory)
        }
    }

    fun createCheckPointUpdate(updateText: String){
        viewModelScope.launch {
            try {
                createCheckPointUpdateUseCase(updateText)
                _updateSaveSuccessLiveData.postValue(true)
            }catch (e: Exception){
                Log.e("jejeje", "Este error ocurrio: ", e)
                _updateSaveSuccessLiveData.postValue(false)
            }
        }
    }

}