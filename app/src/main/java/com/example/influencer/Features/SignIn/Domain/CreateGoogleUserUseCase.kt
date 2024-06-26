package com.example.influencer.Features.SignIn.Domain

import com.example.influencer.Core.Data.Network.AuthenticationService
import com.example.influencer.Core.Data.Network.UserService
import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin
import com.google.android.gms.tasks.Task
import javax.inject.Inject

class CreateGoogleUserUseCase @Inject constructor(
    private val userService: UserService,
    private val authenticationService: AuthenticationService
) {
    fun execute(profilePictureUrl: String?): Task<Void> {
        val email = authenticationService.email
        var username = email.substringBefore('@').take(16)
        val usuarioSignin = UsuarioSignin(email, username, "NO PASSWORD SAVED WITH GOOGLE SIGNIN").apply {
            id = authenticationService.uid
            this.profilePictureUrl = profilePictureUrl
        }
        return userService.crearUsuario(usuarioSignin)
    }
}



