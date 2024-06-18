package com.example.influencer.Features.General_FollowersFollowing_showing.Domain

import com.example.influencer.Core.Data.Repositories.UserRepository.UserRepository
import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUserRelationsUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend fun getFollowers(userId: String): List<UsuarioSignin> = withContext(Dispatchers.IO) {
        userRepository.getFollowers(userId)
    }

    suspend fun getFollowing(userId: String): List<UsuarioSignin> = withContext(Dispatchers.IO) {
        userRepository.getFollowing(userId)
    }
}