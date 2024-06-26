package com.example.influencer.Features.SignIn.UI.GoogleSignin

// https://www.notion.so/re-factoreo-del-Google-Sign-In-5ff74b3cda2f4e7fa67e634f9bd16e20

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.influencer.Core.Utils.Event
import com.example.influencer.Core.Utils.SingleLiveEvent
import com.example.influencer.Features.SignIn.Domain.CheckIfUserExistsUseCase
import com.example.influencer.Features.SignIn.Domain.CreateGoogleUserUseCase
import com.example.influencer.Features.SignIn.Domain.FirebaseAuthWithGoogleUseCase
import com.example.influencer.R
import com.google.android.gms.tasks.Tasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// https://www.notion.so/re-factoreo-del-Google-Sign-In-5ff74b3cda2f4e7fa67e634f9bd16e20
@HiltViewModel
class GoogleSigninViewModel @Inject constructor(
    private val firebaseAuthWithGoogleUseCase: FirebaseAuthWithGoogleUseCase,
    private val checkIfUserExistsUseCase: CheckIfUserExistsUseCase,
    private val createGoogleUserUseCase: CreateGoogleUserUseCase,
    private val resources: Resources
) : ViewModel() {

    val userExists = SingleLiveEvent<Boolean>()
    val userGetsCreated = SingleLiveEvent<Boolean>()

    private val _loading = MutableLiveData<Event<Boolean>>()
    val loading: LiveData<Event<Boolean>> = _loading

    val toastMessage = SingleLiveEvent<String>()

    fun handleGoogleSignInResult(idToken: String, profilePictureUrl: String) {
        _loading.postValue(Event(true))
        firebaseAuthWithGoogleUseCase.execute(idToken)
            .continueWithTask { task ->
                if (task.isSuccessful) {
                    checkIfUserExistsUseCase.execute()
                } else {
                    _loading.postValue(Event(false))
                    toastMessage.value = resources.getString(R.string.Generic_error)
                    throw RuntimeException(task.exception)
                }
            }
            .continueWithTask { task ->
                if (task.isSuccessful) {
                    if (task.result.exists()) {
                        _loading.postValue(Event(false))
                        userExists.postValue(true)
                        Tasks.forResult(null)
                    } else {
                        createGoogleUserUseCase.execute(profilePictureUrl)
                            .addOnSuccessListener {
                                _loading.postValue(Event(false))
                                userGetsCreated.postValue(true)
                            }
                    }
                } else {
                    _loading.postValue(Event(false))
                    toastMessage.value = resources.getString(R.string.FireStore_Error)
                    Log.e("GoogleSigninViewModel", "Error checking user existence in Firestore.", task.exception)
                    throw RuntimeException(task.exception)
                }
            }
            .addOnFailureListener { e ->
                _loading.postValue(Event(false))
                toastMessage.value = resources.getString(R.string.FireStore_Error)
            }
    }
}
