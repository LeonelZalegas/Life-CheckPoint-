package com.example.influencer.Domain

import com.example.influencer.Data.Repositories.UserRepository.UserRepository
import javax.inject.Inject

class GetUserProfilePictureUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): String {
        return userRepository.getUserProfilePictureUrl()
    }
}
