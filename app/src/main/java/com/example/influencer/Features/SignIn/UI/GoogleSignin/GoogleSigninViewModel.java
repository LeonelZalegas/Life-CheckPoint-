package com.example.influencer.Features.SignIn.UI.GoogleSignin;

import android.content.res.Resources;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.influencer.Core.Data.Network.AuthenticationService;
import com.example.influencer.Core.Utils.Event;
import com.example.influencer.Core.Utils.SingleLiveEvent;
import com.example.influencer.Features.SignIn.Domain.CheckIfUserExistsUseCase;
import com.example.influencer.Features.SignIn.Domain.CreateGoogleUserUseCase;
import com.example.influencer.Features.SignIn.Domain.FirebaseAuthWithGoogleUseCase;
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
    private final AuthenticationService authenticationService; //eliminar luego de ver q bug esta solucionado
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
                                 AuthenticationService authenticationService,//eliminar luego de ver q bug esta solucionado
                                 Resources resources) {

        this.firebaseAuthWithGoogleUseCase = firebaseAuthWithGoogleUseCase;
        this.checkIfUserExistsUseCase = checkIfUserExistsUseCase;
        this.createGoogleUserUseCase = createGoogleUserUseCase;
        this.authenticationService = authenticationService; //eliminar luego de ver q bug esta solucionado
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
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            _Loading.postValue(new Event<>(false));
                            userExists.postValue(true);
                            return Tasks.forResult(null); // Avoid triggering user creation flow
                        } else {
                            Log.d("GoogleSigninViewModel", "User does not exist in Firestore, creating new user. UID: " + authenticationService.getUid()); //eliminar luego de ver q bug esta solucionado
                            return createGoogleUserUseCase.execute(profilePictureUrl)
                                    .addOnSuccessListener(result -> {
                                        _Loading.postValue(new Event<>(false));
                                        userGetsCreated.postValue(true);
                                    });
                        }
                    } else {
                        _Loading.postValue(new Event<>(false));
                        ToastMessage.setValue(resources.getString(R.string.FireStore_Error));
                        Log.e("GoogleSigninViewModel", "Error checking user existence in Firestore.", task.getException()); //eliminar luego de ver q bug esta solucionado
                        throw new RuntimeException(task.getException());
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
