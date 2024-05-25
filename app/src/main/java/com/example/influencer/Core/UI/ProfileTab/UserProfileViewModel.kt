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
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = SingleLiveEvent<Result<UsuarioSignin>>()
    val user: LiveData<Result<UsuarioSignin>> get() = _user

    private val _isCurrentUser = MutableLiveData<Boolean>()
    val isCurrentUser: LiveData<Boolean> = _isCurrentUser

    private val _isFollowing = MutableLiveData<Boolean>()
    val isFollowing: LiveData<Boolean> = _isFollowing

    fun loadUser(userId: String?) {
        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUserId()
            Log.w("paloma", "el userID es: $userId y el currentUserId es: $currentUserId ")
            _isCurrentUser.value = (userId == currentUserId || userId == null )

            val result = getUserByIdUseCase(userId)
            _user.value = result

            if (userId != null && userId != currentUserId) {
                _isFollowing.value = userRepository.isFollowing(currentUserId, userId)
            }
        }
    }

    fun followUser(targetUserId: String?) {
        viewModelScope.launch {
            targetUserId?.let {
                userRepository.followUser(targetUserId)
                _isFollowing.value = true
            }
        }
    }

    fun unfollowUser(targetUserId: String?) {
        viewModelScope.launch {
            targetUserId?.let {
                userRepository.unfollowUser(targetUserId)
                _isFollowing.value = false
            }
        }
    }
}
