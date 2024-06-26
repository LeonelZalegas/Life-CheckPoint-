package com.example.influencer.Features.Login.UI

import android.content.res.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.influencer.Features.Login.Domain.LoginUseCase
import androidx.lifecycle.ViewModel
import com.example.influencer.Core.Utils.SingleLiveEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.influencer.Core.Utils.Event
import com.example.influencer.Features.Login.Domain.Model.UsuarioLogin
import com.example.influencer.R


import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val resources: Resources,
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _toastMessage = SingleLiveEvent<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>> = _isLoading

    private val _navigateToSignIn = MutableLiveData<Event<Unit>>()
    val navigateToSignIn: LiveData<Event<Unit>> = _navigateToSignIn

    private val _navigateToGoogleSignIn = MutableLiveData<Event<Unit>>()
    val navigateToGoogleSignIn: LiveData<Event<Unit>> = _navigateToGoogleSignIn

    private val _navigateToHome = MutableLiveData<Event<Unit>>()
    val navigateToHome: LiveData<Event<Unit>> = _navigateToHome

    fun onLoginSelected(usuarioLogin: UsuarioLogin) {
        viewModelScope.launch {
            _isLoading.value = Event(true)
            try {
                loginUseCase(usuarioLogin)
                _navigateToHome.value = Event(Unit)
            } catch (e: Exception) {
                _toastMessage.value = resources.getString(R.string.error_LogIn)
            } finally {
                _isLoading.value = Event(false)
            }
        }
    }

    fun onSignInSelected() {
        _navigateToSignIn.value = Event(Unit)
    }

    fun onGoogleSignInSelected() {
        _navigateToGoogleSignIn.value = Event(Unit)
    }

    fun validateLogin(email: String?, password: String?): Boolean {
        _isLoading.value = Event(true)
        return if (email.isNullOrBlank() || password.isNullOrBlank()) {
            _isLoading.value = Event(false)
            _toastMessage.value = resources.getString(R.string.empty_fields_LogIn)
            false
        } else {
            true
        }
    }
}
