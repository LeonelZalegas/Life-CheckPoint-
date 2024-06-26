package com.example.influencer.Features.OnBoarding___CountryAndDateSelector.UI.OnBoarding

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.influencer.Core.Utils.Event

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

    private val _navigateToHome = MutableLiveData<Event<Unit>>()
    val navigateToHome: LiveData<Event<Unit>> = _navigateToHome

    fun startNowSelected() {
        _navigateToHome.value = Event(Unit)
    }
}