package com.example.influencer.UI.Upload_New_Checkpoint

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.influencer.Domain.SavePostUseCase
import com.example.influencer.Domain.UploadImageUseCase
import com.example.influencer.UI.Upload_New_Checkpoint.Model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadCheckpointViewModel @Inject constructor(
    private val savePostUseCase: SavePostUseCase,
    private val uploadImageUseCase: UploadImageUseCase
) : ViewModel() {

    var _tempImageUri1: Uri? = null
    var _tempImageUri2: Uri? = null
    private val _imagesLiveData = MutableLiveData<List<Uri?>>()
    val imagesLiveData: LiveData<List<Uri?>> = _imagesLiveData


    fun onCameraIconClicked(photoUri: Uri?){
        viewModelScope.launch{
            try {
                if (_tempImageUri1 == null) {
                    _tempImageUri1 = photoUri
                } else {
                    _tempImageUri2 = photoUri
                }
                updateLiveData()
            } catch (e: Exception){
                println(e.message)
            }
        }
    }

    fun canTakeMorePictures(): Boolean {
        return _tempImageUri1 == null || _tempImageUri2 == null
    }

    fun savePost(text: String, satisfactionLevel: Int){
        viewModelScope.launch {
            val image1Url = _tempImageUri1?.let { uploadImageUseCase(it)}
            val image2Url = _tempImageUri2?.let { uploadImageUseCase(it)}

            val post = Post(text, satisfactionLevel, image1Url, image2Url)
            savePostUseCase(post)
        }
    }

    fun removeImageAt(position: Int){
        when (position) {
            0 -> _tempImageUri1 = null
            1 -> _tempImageUri2 = null
        }
        updateLiveData()
    }

    private fun updateLiveData() {
        _imagesLiveData.value = listOfNotNull(_tempImageUri1, _tempImageUri2)
    }


}