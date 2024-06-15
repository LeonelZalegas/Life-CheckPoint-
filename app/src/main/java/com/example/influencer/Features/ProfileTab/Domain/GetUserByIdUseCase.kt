package com.example.influencer.Features.ProfileTab.Domain

import com.example.influencer.Core.Data.Repositories.UserRepository.FirestoreUserRepository
import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val firestoreUserRepository: FirestoreUserRepository
) {
    suspend operator fun invoke(userId: String?): Result<UsuarioSignin> {
        return firestoreUserRepository.getUserById(userId)
    }
}
