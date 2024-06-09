package com.example.influencer.Core.UI.ProfileTab

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.influencer.Core.Data.Repositories.UserRepository.UserRepository
import com.example.influencer.Core.Domain.CheckpointLikesTabUseCase
import com.example.influencer.Core.Domain.DeletePostUseCase
import com.example.influencer.Core.Domain.GetUserByIdUseCase
import com.example.influencer.Core.Utils.SingleLiveEvent
import com.example.influencer.Features.CheckPoint_Tab.Domain.LikesInteractionsUseCase
import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin
import com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val userRepository: UserRepository, //https://www.notion.so/ProfileTabFragment-26cfd73f6c2c4576b680f713ad431eaf?pvs=4#9b7af75af45f4b5694f2bd4fded4ab36
    private val checkpointLikesTabUseCase: CheckpointLikesTabUseCase,
    private val likesInteractionsUseCase: LikesInteractionsUseCase,
    private val deletePostUseCase: DeletePostUseCase
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

    private val _profileTabUserId = MutableLiveData<String?>()
    val profileTabUserId: LiveData<String?> get() = _profileTabUserId

    val loading = MutableLiveData<Boolean>()

    val progress = MutableLiveData<Boolean>()

    fun loadUser(userId: String?) {
        viewModelScope.launch {
            val result = getUserByIdUseCase(userId)
            _user.value = result

            val currentUserId = userRepository.getCurrentUserId()  // es el Id del usuario que esta logeado y esta usando la app como tal
            _isCurrentUser.value = (userId == currentUserId || userId == null ) //recordar q si userId == null entonces se trata del Id del owner user

            if (userId != null && userId != currentUserId) {
                _isFollowing.value = userRepository.isFollowing(currentUserId, userId)
            }

            if (userId == currentUserId || userId == null)
                _profileTabUserId.value = currentUserId  //userId es el id del perfil que clickeamos, necesitamos cargar este en profileTabUserId para avisar q ya se ejecuto loadUser y hay 1 valor en userId para utilizarlo en loadCheckpointsByCategory
                else
                    _profileTabUserId.value = userId  //userId es el id del perfil que clickeamos, necesitamos cargar este en profileTabUserId para avisar q ya se ejecuto loadUser y hay 1 valor en userId para utilizarlo en loadCheckpointsByCategory
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
            progress.postValue(true)
            profileTabUserId.value?.let { UserId ->
                val result = checkpointLikesTabUseCase.getUserPostsByCategory(UserId, category)
                _checkpointsPosts.value = result
            }
            progress.postValue(false)
        }
    }

    fun loadLikes() {
        viewModelScope.launch {
            progress.postValue(true)
            profileTabUserId.value?.let { UserId ->
                val result = checkpointLikesTabUseCase.getUserLikedPosts(UserId)
                Log.w("noPosts", "el resultado de los likes es:$result ")
                _likesPosts.value = result
            }
            progress.postValue(false)
        }
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            try {
                likesInteractionsUseCase.likePost(postId)
            } catch (e: Exception) {
                Log.e("ViewModel", "Error liking post", e)
            }
        }
    }

    fun unlikePost(postId: String) {
        viewModelScope.launch {
            try {
                likesInteractionsUseCase.unlikePost(postId)
            } catch (e: Exception) {
                Log.e("ViewModel", "Error unliking post", e)
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
                deletePostUseCase(postId)
        }
    }
}
