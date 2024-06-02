package com.example.influencer.Core.UI.ProfileTab

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.influencer.Core.Data.Repositories.UserRepository.UserRepository
import com.example.influencer.Core.Domain.CheckpointLikesTabUseCase
import com.example.influencer.Core.Domain.GetUserByIdUseCase
import com.example.influencer.Core.Utils.SingleLiveEvent
import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin
import com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val userRepository: UserRepository, //https://www.notion.so/ProfileTabFragment-26cfd73f6c2c4576b680f713ad431eaf?pvs=4#9b7af75af45f4b5694f2bd4fded4ab36
    private val checkpointLikesTabUseCase: CheckpointLikesTabUseCase
) : ViewModel() {

    private val _user = SingleLiveEvent<Result<UsuarioSignin>>()
    val user: LiveData<Result<UsuarioSignin>> get() = _user

    private val _isCurrentUser = MutableLiveData<Boolean>()
    val isCurrentUser: LiveData<Boolean> = _isCurrentUser

    private val _isFollowing = MutableLiveData<Boolean>()
    val isFollowing: LiveData<Boolean> = _isFollowing

    private val _checkpointsPosts = MutableLiveData<List<Post>?>()
    val checkpointsPosts: LiveData<List<Post>?> get() = _checkpointsPosts

    private val _likesPosts = MutableLiveData<List<Post>?>()
    val likesPosts: LiveData<List<Post>?> get() = _likesPosts

    private var profileTabUserId: String? = null

    val loading = MutableLiveData<Boolean>()

    fun loadUser(userId: String?) {
        profileTabUserId = userId

        viewModelScope.launch {
            val result = getUserByIdUseCase(userId)
            _user.value = result

            val currentUserId = userRepository.getCurrentUserId()  // es el Id del usuario que esta logeado y esta usando la app como tal
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

    fun loadCheckpointsByCategory(category: String) {
        viewModelScope.launch {
            profileTabUserId?.let { profileTabUserId ->
                val result = checkpointLikesTabUseCase.getUserPostsByCategory(profileTabUserId, category)
                _checkpointsPosts.value = result
            }
        }
    }

    fun loadLikes() {
        viewModelScope.launch {
            profileTabUserId?.let { profileTabUserId ->
                val result = checkpointLikesTabUseCase.getUserLikedPosts(profileTabUserId)
                _likesPosts.value = result
            }
        }
    }

}
