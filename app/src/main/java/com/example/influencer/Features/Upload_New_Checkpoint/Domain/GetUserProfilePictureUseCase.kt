package com.example.influencer.Features.Upload_New_Checkpoint.Domain

import com.example.influencer.LaNuevaEstr.Core.Data.Repositories.UserRepository.UserRepository
import javax.inject.Inject

class GetUserProfilePictureUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): String {
        return userRepository.getUserProfilePictureUrl()
    }
}
