package com.example.influencer.Core.UI.ProfileTab

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.influencer.Core.Domain.GetUserByIdUseCase
import com.example.influencer.Core.Utils.SingleLiveEvent
import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {

    private val _user = SingleLiveEvent<Result<UsuarioSignin>>()
    val user: LiveData<Result<UsuarioSignin>> get() = _user

    fun loadUser(userId: String?) {
        viewModelScope.launch {
            val result = getUserByIdUseCase(userId)
            _user.value = result
        }
    }
}
