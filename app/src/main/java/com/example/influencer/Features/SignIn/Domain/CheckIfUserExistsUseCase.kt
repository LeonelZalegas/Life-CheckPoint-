package com.example.influencer.Features.SignIn.Domain

import javax.inject.Inject
import com.example.influencer.Core.Data.Network.UserService
import com.example.influencer.Core.Data.Network.AuthenticationService
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

class CheckIfUserExistsUseCase @Inject constructor(
    private val userService: UserService,
    private val authenticationService: AuthenticationService
) {
    fun execute(): Task<DocumentSnapshot> {
        val userId = authenticationService.getUid()
        return userService.getUsuario(userId)
    }
}