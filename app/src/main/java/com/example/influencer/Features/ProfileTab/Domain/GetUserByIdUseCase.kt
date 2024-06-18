package com.example.influencer.Features.ProfileTab.Domain

import com.example.influencer.Core.Data.Repositories.UserRepository.FirestoreUserRepository
import com.example.influencer.Core.Data.Repositories.UserRepository.UserRepository
import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String?): Result<UsuarioSignin> {
        return userRepository.getUserById(userId)
    }
}
