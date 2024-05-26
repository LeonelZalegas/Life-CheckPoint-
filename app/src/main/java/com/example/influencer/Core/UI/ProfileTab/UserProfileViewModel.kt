package com.example.influencer.Core.UI.ProfileTab

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.influencer.Core.Data.Repositories.UserRepository.UserRepository
import com.example.influencer.Core.Domain.GetUserByIdUseCase
import com.example.influencer.Core.Utils.SingleLiveEvent
import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val userRepository: UserRepository  //https://www.notion.so/ProfileTabFragment-26cfd73f6c2c4576b680f713ad431eaf?pvs=4#9b7af75af45f4b5694f2bd4fded4ab36
) : ViewModel() {

    private val _user = SingleLiveEvent<Result<UsuarioSignin>>()
    val user: LiveData<Result<UsuarioSignin>> get() = _user

    private val _isCurrentUser = MutableLiveData<Boolean>()
    val isCurrentUser: LiveData<Boolean> = _isCurrentUser

    private val _isFollowing = MutableLiveData<Boolean>()
    val isFollowing: LiveData<Boolean> = _isFollowing

    val loading = MutableLiveData<Boolean>()

    fun loadUser(userId: String?) {
        viewModelScope.launch {
            val result = getUserByIdUseCase(userId)
            _user.value = result

            val currentUserId = userRepository.getCurrentUserId()
            _isCurrentUser.value = (userId == currentUserId || userId == null ) //recordar q si userId == null entonces se trata del Id del owner user

            if (userId != null && userId != currentUserId) {
                _isFollowing.value = userRepository.isFollowing(currentUserId, userId)
            }
        }
    }

    fun followUser(targetUserId: String?) {
        viewModelScope.launch {
            targetUserId?.let {
                loading.postValue(true)
                userRepository.followUser(targetUserId)
                _isFollowing.value = true
                loading.postValue(false)
            }
        }
    }

    fun unfollowUser(targetUserId: String?) {
        viewModelScope.launch {
            targetUserId?.let {
                loading.postValue(true)
                userRepository.unfollowUser(targetUserId)
                _isFollowing.value = false
                loading.postValue(false)
            }
        }
    }
}
