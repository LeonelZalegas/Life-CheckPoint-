package com.example.influencer.Features.OnBoarding___CountryAndDateSelector.UI.OnBoarding;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.influencer.Core.Utils.Event;

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

