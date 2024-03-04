package com.example.influencer.UI.SignIn.GoogleSignin;

import android.content.res.Resources;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.influencer.Core.Event;
import com.example.influencer.Core.SingleLiveEvent;
import com.example.influencer.Domain.GoogleSigninUSECASES.CheckIfUserExistsUseCase;
import com.example.influencer.Domain.GoogleSigninUSECASES.CreateGoogleUserUseCase;
import com.example.influencer.Domain.GoogleSigninUSECASES.FirebaseAuthWithGoogleUseCase;
import com.example.influencer.R;
import com.google.android.gms.tasks.Tasks;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

//https://www.notion.so/re-factoreo-del-Google-Sign-In-5ff74b3cda2f4e7fa67e634f9bd16e20
@HiltViewModel
public class GoogleSigninViewModel extends ViewModel {

    private final FirebaseAuthWithGoogleUseCase firebaseAuthWithGoogleUseCase;
    private final CheckIfUserExistsUseCase checkIfUserExistsUseCase;
    private final CreateGoogleUserUseCase createGoogleUserUseCase;
    private final Resources resources;

    SingleLiveEvent<Boolean> userExists = new SingleLiveEvent<>();
    SingleLiveEvent<Boolean> userGetsCreated = new SingleLiveEvent<>();

    private final MutableLiveData<Event<Boolean>> _Loading = new MutableLiveData<>();
    public LiveData<Event<Boolean>> Loading = _Loading;

    private final SingleLiveEvent<String> ToastMessage = new SingleLiveEvent<>();

    @Inject
    public GoogleSigninViewModel(FirebaseAuthWithGoogleUseCase firebaseAuthWithGoogleUseCase,
                                 CheckIfUserExistsUseCase checkIfUserExistsUseCase,
                                 CreateGoogleUserUseCase createGoogleUserUseCase,
                                 Resources resources) {

        this.firebaseAuthWithGoogleUseCase = firebaseAuthWithGoogleUseCase;
        this.checkIfUserExistsUseCase = checkIfUserExistsUseCase;
        this.createGoogleUserUseCase = createGoogleUserUseCase;
        this.resources = resources;
    }

    public SingleLiveEvent<Boolean> getUserExists() {
        return userExists;
    }

    public SingleLiveEvent<Boolean> getUserGetsCreated() {
        return userGetsCreated;
    }


    public void handleGoogleSignInResult(String idToken,String profilePictureUrl) {
        _Loading.postValue(new Event<>(true));
        firebaseAuthWithGoogleUseCase.execute(idToken)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return checkIfUserExistsUseCase.execute();
                    } else {
                        _Loading.postValue(new Event<>(false));
                        ToastMessage.setValue(resources.getString(R.string.Generic_error));
                        throw new RuntimeException(task.getException());  //esto es para tirar otro error supongo xd
                    }
                })
                .continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        _Loading.postValue(new Event<>(false));
                        userExists.postValue(true);
                        return Tasks.forResult(null); // Return a completed Task to avoid triggering user creation flow
                    } else {
                        return createGoogleUserUseCase.execute(profilePictureUrl)
                                .addOnSuccessListener(result -> {
                                    _Loading.postValue(new Event<>(false));
                                    userGetsCreated.postValue(true);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    _Loading.postValue(new Event<>(false));
                    ToastMessage.setValue(resources.getString(R.string.FireStore_Error));
                });
    }

    public SingleLiveEvent<String> getToastMessage() {
        return ToastMessage;
    }
}
