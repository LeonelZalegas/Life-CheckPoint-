package com.example.influencer.Features.SignIn.Domain

import javax.inject.Inject
import com.example.influencer.Core.Data.Network.AuthenticationService
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.tasks.await

class FirebaseAuthWithGoogleUseCase @Inject constructor(
    private val authenticationService: AuthenticationService
) {
    fun execute(idToken: String): Task<AuthResult> {
        return authenticationService.googleSignin(idToken)
    }
}