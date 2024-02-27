package com.example.influencer.UI.OnBoarding___CountryAndDateSelector.OnBoarding;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.influencer.Core.Event;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class OnboardingViewModel extends ViewModel {

    private final MutableLiveData<Event<Boolean>> _navigateToHome = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToHome  = _navigateToHome ;

    @Inject
    public OnboardingViewModel() {
        //no dependencies needed
    }

    public void StartnowSelected(){
        _navigateToHome.postValue(new Event<>(true));
    }
}

