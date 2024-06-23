package com.example.influencer.Features.Settings.UI

import android.content.res.Resources
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.influencer.Features.Settings.Domain.LogoutUseCase
import com.example.influencer.Features.Settings.Domain.SaveNewUsernameUseCase
import com.example.influencer.Features.Settings.Domain.UpdateProfilePictureUseCase
import com.example.influencer.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val saveNewUsernameUseCase: SaveNewUsernameUseCase,
    private val updateProfilePictureUseCase: UpdateProfilePictureUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val resources: Resources
) : ViewModel() {

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    private val _logoutStatus = MutableLiveData<Boolean>()
    val logoutStatus: LiveData<Boolean> = _logoutStatus

    val loading = MutableLiveData<Boolean>()

    fun updateUserName(newUsername: String){
        viewModelScope.launch {
            try {
                loading.postValue(true)
                saveNewUsernameUseCase.invoke(newUsername)
                loading.postValue(false)
                _statusMessage.postValue(resources.getString(R.string.SuccesUsername_Title))
            }catch (e: Exception) {
                _statusMessage.postValue("There has been an error updating the Username")
            }
        }
    }

    fun updateProfilePicture(imageUri: Uri) {
        viewModelScope.launch {
            try {
                loading.postValue(true)
                updateProfilePictureUseCase(imageUri)
                loading.postValue(false)
                _statusMessage.postValue(resources.getString(R.string.SuccesProfPic_Title))
            } catch (e: Exception) {
                _statusMessage.postValue("Failed to update profile picture: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                logoutUseCase()
                _logoutStatus.postValue(true)
            } catch (e: Exception) {
                _logoutStatus.postValue(false)
            }
        }
    }
}