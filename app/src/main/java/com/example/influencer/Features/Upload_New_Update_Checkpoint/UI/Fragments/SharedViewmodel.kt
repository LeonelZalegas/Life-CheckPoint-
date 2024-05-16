package com.example.influencer.Features.Upload_New_Update_Checkpoint.UI.Fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.influencer.Features.Create_Modify_Checkpoint_Menu.Domain.Model.CheckpointThemeItem
import com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model.Post
import com.example.influencer.Features.Upload_New_Update_Checkpoint.Domain.CreateCheckPointUpdateUseCase
import com.example.influencer.Features.Upload_New_Update_Checkpoint.Domain.GetNextUpdateNumberUseCase
import com.example.influencer.Features.Upload_New_Update_Checkpoint.Domain.GetPostAndUpdatesUseCase
import com.example.influencer.Features.Upload_New_Update_Checkpoint.Domain.Model.CheckPoint_Update_Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewmodel @Inject constructor(
    private val createCheckPointUpdateUseCase: CreateCheckPointUpdateUseCase,
    private val getNextUpdateNumberUseCase: GetNextUpdateNumberUseCase,
    private val getPostAndUpdatesUseCase: GetPostAndUpdatesUseCase
) : ViewModel() {

    private val _selectedCategory = MutableLiveData<CheckpointThemeItem>()
    val selectedCategory: LiveData<CheckpointThemeItem> =_selectedCategory

    private val _postAndUpdates = MutableLiveData<Result<Pair<Post, List<CheckPoint_Update_Item>>>>()
    val postAndUpdates: LiveData<Result<Pair<Post, List<CheckPoint_Update_Item>>>> = _postAndUpdates

    private val _nextUpdateNumber = MutableLiveData<Int?>()
    val nextUpdateNumber:LiveData<Int?> = _nextUpdateNumber

    private val _updateSaveSuccessLiveData = MutableLiveData<Boolean>()
    val updateSaveSuccessLiveData: LiveData<Boolean> = _updateSaveSuccessLiveData

    val loading = MutableLiveData<Boolean>()

    fun setSelectedCategory(category: CheckpointThemeItem) {
        _selectedCategory.value = category
    }

    fun getNextUpdateNumber(selectedCategory: String){
        viewModelScope.launch {
            _nextUpdateNumber.value = getNextUpdateNumberUseCase(selectedCategory)
        }
    }

    fun createCheckPointUpdate(updateText: String){
        viewModelScope.launch {
            try {
                loading.postValue(true)
                createCheckPointUpdateUseCase(updateText)
                _updateSaveSuccessLiveData.postValue(true)
            }catch (e: Exception){
                _updateSaveSuccessLiveData.postValue(false)
            }
            loading.postValue(false)
        }
    }

    fun loadPostAndUpdates() {
        val category = _selectedCategory.value?.text
        viewModelScope.launch {
            val result = category?.let { getPostAndUpdatesUseCase(it) }
            _postAndUpdates.value = result!!
        }
    }
}