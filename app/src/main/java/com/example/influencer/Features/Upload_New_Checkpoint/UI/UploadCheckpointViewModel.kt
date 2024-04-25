package com.example.influencer.Features.Upload_New_Checkpoint.UI

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.influencer.LaNuevaEstr.Features.Upload_New_Checkpoint.Domain.GetUserProfilePictureUseCase
import com.example.influencer.LaNuevaEstr.Features.Upload_New_Checkpoint.Domain.GetLastThreeImagesUseCase
import com.example.influencer.LaNuevaEstr.Features.Upload_New_Checkpoint.Domain.SavePostUseCase
import com.example.influencer.LaNuevaEstr.Features.Upload_New_Checkpoint.Domain.UploadImageUseCase
import com.example.influencer.LaNuevaEstr.Features.Upload_New_Checkpoint.Domain.Model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadCheckpointViewModel @Inject constructor(
    private val savePostUseCase: SavePostUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val getLastThreeImagesUseCase: GetLastThreeImagesUseCase,
    private val getUserProfilePictureUseCase: GetUserProfilePictureUseCase
) : ViewModel() {

    private val _recyclerViewLiveData = MutableLiveData<MutableList<Uri?>>(mutableListOf())
    val recyclerViewLiveData: LiveData<MutableList<Uri?>> = _recyclerViewLiveData

    private val _lastThreeImagesLiveData = MutableLiveData<List<Uri>>()
    val lastThreeImagesLiveData: LiveData<List<Uri>> = _lastThreeImagesLiveData

    val loading = MutableLiveData<Boolean>()

    private val _postSaveSuccessLiveData = MutableLiveData<Boolean>()
    val postSaveSuccessLiveData: LiveData<Boolean> = _postSaveSuccessLiveData

    private val _profilePictureUrl = MutableLiveData<String>()
    val profilePictureUrl: LiveData<String> = _profilePictureUrl


    fun uploadImageRecyclerView(photoUri: Uri?){
        viewModelScope.launch{
            try {
                _recyclerViewLiveData.value?.let { images ->  //ingresamos a la lista de interna del recyclerview
                    if (images.size <  2) {
                        images.add(photoUri)
                        _recyclerViewLiveData.postValue(images)// Notify observers, esto es como el SetValue() para avisar al observer de los datos cambiados en tiempo real
                    }
                }
            } catch (e: Exception){
                println(e.message)
            }
        }
    }

    fun canTakeMorePictures(): Boolean {
        val images = _recyclerViewLiveData.value
        // Check if the list is null or empty first. If so, it can definitely take more pictures.
        if (images.isNullOrEmpty()) {
            return true
        }
        // If the list is not null or empty, check if its size is less than 2.
        return images.size < 2
    }

    fun savePost(text: String, satisfactionLevel: Int,selectedCategoryText: String,selectedCategoryColor: String){
        viewModelScope.launch {
            try {
                loading.postValue(true)
                val image1Url = _recyclerViewLiveData.value?.getOrNull(0)?.let { uploadImageUseCase(it) }   //subimos imagenes a Firebase STORAGE
                val image2Url = _recyclerViewLiveData.value?.getOrNull(1)?.let { uploadImageUseCase(it) }

                val post = Post(text, satisfactionLevel, image1Url, image2Url,selectedCategoryText,selectedCategoryColor) // This now suspends until completion
                savePostUseCase(post)
                _postSaveSuccessLiveData.postValue(true) // Post success on completion
            }catch (e: Exception){
                _postSaveSuccessLiveData.postValue(false) // Post failure on exception
            }
              loading.postValue(false)
        }
    }

    fun removeImageAt(position: Int){
        _recyclerViewLiveData.value?.let { list ->
            list[position] = null
            _recyclerViewLiveData.value = list.filterNotNull().toMutableList() // Notify observers
        }

    }

    fun fetchLastThreeImagesUris() {
        viewModelScope.launch {
            _lastThreeImagesLiveData.value = getLastThreeImagesUseCase()
        }
    }

    fun fetchUserProfilePicture() {
        viewModelScope.launch {
            val url = getUserProfilePictureUseCase()
            _profilePictureUrl.postValue(url)
        }
    }

}