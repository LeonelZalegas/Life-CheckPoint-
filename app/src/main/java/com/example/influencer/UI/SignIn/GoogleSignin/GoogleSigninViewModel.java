package com.example.influencer.UI.SignIn.GoogleSignin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.influencer.Core.Event;
import com.example.influencer.Core.MyApp;
import com.example.influencer.Core.SingleLiveEvent;
import com.example.influencer.Data.Network.AuthenticationService;
import com.example.influencer.Data.Network.FirebaseClient;
import com.example.influencer.Data.Network.UserService;
import com.example.influencer.Domain.CheckIfUserExistsUseCase;
import com.example.influencer.Domain.CreateGoogleUserUseCase;
import com.example.influencer.Domain.FirebaseAuthWithGoogleUseCase;
import com.example.influencer.R;
import com.google.android.gms.tasks.Tasks;
//https://www.notion.so/re-factoreo-del-Google-Sign-In-5ff74b3cda2f4e7fa67e634f9bd16e20
public class GoogleSigninViewModel extends ViewModel {

    private final FirebaseAuthWithGoogleUseCase firebaseAuthWithGoogleUseCase;
    private final CheckIfUserExistsUseCase checkIfUserExistsUseCase;
    private final CreateGoogleUserUseCase createGoogleUserUseCase;

    SingleLiveEvent<Boolean> userExists = new SingleLiveEvent<>();

    private final MutableLiveData<Event<Boolean>> _Loading = new MutableLiveData<>();
    public LiveData<Event<Boolean>> Loading = _Loading;

    private final SingleLiveEvent<String> ToastMessage = new SingleLiveEvent<>();

    public GoogleSigninViewModel() {
        AuthenticationService authService = AuthenticationService.getInstance();
        UserService userService = new UserService(FirebaseClient.getInstance());

        firebaseAuthWithGoogleUseCase = new FirebaseAuthWithGoogleUseCase(authService);
        checkIfUserExistsUseCase = new CheckIfUserExistsUseCase(userService, authService);
        createGoogleUserUseCase = new CreateGoogleUserUseCase(userService, authService);
    }

    public SingleLiveEvent<Boolean> getUserExists() {
        return userExists;
    }

    public void handleGoogleSignInResult(String idToken) {
        _Loading.postValue(new Event<>(true));
        firebaseAuthWithGoogleUseCase.execute(idToken)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return checkIfUserExistsUseCase.execute();
                    } else {
                        _Loading.postValue(new Event<>(false));
                        ToastMessage.setValue(MyApp.getInstance().getAString(R.string.Generic_error));
                        throw new RuntimeException(task.getException());  //esto es para tirar otro error supongo xd
                    }
                })
                .continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        _Loading.postValue(new Event<>(false));
                        userExists.postValue(true);
                        return Tasks.forResult(null);
                    } else {
                        return createGoogleUserUseCase.execute();
                    }
                })
                .addOnSuccessListener(result -> {
                    _Loading.postValue(new Event<>(false));
                    userExists.postValue(true);
                })
                .addOnFailureListener(e -> {
                    _Loading.postValue(new Event<>(false));
                    ToastMessage.setValue(MyApp.getInstance().getAString(R.string.FireStore_Error));
                });
    }

    public SingleLiveEvent<String> getToastMessage() {
        return ToastMessage;
    }
}
