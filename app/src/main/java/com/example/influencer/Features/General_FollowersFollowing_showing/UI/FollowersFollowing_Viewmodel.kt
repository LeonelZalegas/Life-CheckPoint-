package com.example.influencer.Features.General_FollowersFollowing_showing.UI

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.influencer.Core.Data.Repositories.UserRepository.UserRepository
import com.example.influencer.Features.CheckPoint_Tab.Domain.Model.CardData
import com.example.influencer.Features.General_FollowersFollowing_showing.Domain.GetUserRelationsUseCase
import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowersFollowing_Viewmodel @Inject constructor(
    private val getUserRelationsUseCase: GetUserRelationsUseCase,
    private val userRepository: UserRepository,
) : ViewModel() {

    val loading = MutableLiveData<Boolean>()

    private val _usersList = MutableLiveData<List<UsuarioSignin>>()
    val usersList: LiveData<List<UsuarioSignin>> = _usersList

    fun getFollowingUsers(currentUserId: String){
        viewModelScope.launch {
            loading.postValue(true)
            _usersList.value = getUserRelationsUseCase.getFollowing(currentUserId)
            loading.postValue(false)
        }
    }

    fun getFollowersUsers(currentUserId: String){
        viewModelScope.launch {
            loading.postValue(true)
            _usersList.value = getUserRelationsUseCase.getFollowers(currentUserId)
            loading.postValue(false)
        }
    }

     fun checkIfFollowing(currentUserId:String, targetUserId:String, callback: (Boolean) -> Unit ){
        viewModelScope.launch {
            val isFollowing = userRepository.isFollowing(currentUserId, targetUserId)
            callback(isFollowing)
        }
    }

    fun followUser(targetUserId: String?) {
        viewModelScope.launch {
            targetUserId?.let {
                loading.postValue(true)
                userRepository.followUser(targetUserId)
                loading.postValue(false)
            }
        }
    }

    fun unfollowUser(targetUserId: String?) {
        viewModelScope.launch {
            targetUserId?.let {
                loading.postValue(true)
                userRepository.unfollowUser(targetUserId)
                loading.postValue(false)
            }
        }
    }

}