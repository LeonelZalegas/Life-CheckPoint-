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

    private val _imagesLiveData = MutableLiveData<MutableList<Uri?>>(mutableListOf())
    val imagesLiveData: LiveData<MutableList<Uri?>> = _imagesLiveData

    private val _postSaveSuccessLiveData = MutableLiveData<Boolean>()
    val postSaveSuccessLiveData: LiveData<Boolean> = _postSaveSuccessLiveData


    fun onCameraIconClicked(photoUri: Uri?){
        viewModelScope.launch{
            try {
                _imagesLiveData.value?.let { images ->  //ingresamos a la lista de _imagesLiveData
                    if (images.size <  2) {
                        images.add(photoUri)
                        _imagesLiveData.postValue(images)// Notify observers, esto es como el SetValue() para avisar al observer de los datos cambiados en tiempo real
                    }
                }
            } catch (e: Exception){
                println(e.message)
            }
        }
    }

    fun canTakeMorePictures(): Boolean {
        val images = _imagesLiveData.value
        // Check if the list is null or empty first. If so, it can definitely take more pictures.
        if (images.isNullOrEmpty()) {
            return true
        }
        // If the list is not null or empty, check if its size is less than 2.
        return images.size < 2
    }

    fun savePost(text: String, satisfactionLevel: Int){
        viewModelScope.launch {
            try {
                val image1Url = _imagesLiveData.value?.getOrNull(0)?.let { uploadImageUseCase(it) }
                val image2Url = _imagesLiveData.value?.getOrNull(1)?.let { uploadImageUseCase(it) }

                val post = Post(text, satisfactionLevel, image1Url, image2Url) // This now suspends until completion
                savePostUseCase(post)
                _postSaveSuccessLiveData.postValue(true) // Post success on completion
            }catch (e: Exception){
                _postSaveSuccessLiveData.postValue(false) // Post failure on exception
            }
        }
    }

    fun removeImageAt(position: Int){
        _imagesLiveData.value?.let { list ->
            list[position] = null
            _imagesLiveData.value = list.filterNotNull().toMutableList() // Notify observers
        }

    }

}