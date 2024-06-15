package com.example.influencer.Features.ProfileTab.Domain

import com.example.influencer.Core.Data.Repositories.PostRepository.PostRepository
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String) {
        postRepository.deletePost(postId)
    }
}