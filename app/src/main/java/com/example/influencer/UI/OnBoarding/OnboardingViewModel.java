package com.example.influencer.UI.OnBoarding;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.influencer.Core.Event;

public class OnboardingViewModel extends ViewModel {

    private final MutableLiveData<Event<Boolean>> _navigateToHome = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToHome  = _navigateToHome ;

    public void StartnowSelected(){
        _navigateToHome.postValue(new Event<>(true));
    }
}

