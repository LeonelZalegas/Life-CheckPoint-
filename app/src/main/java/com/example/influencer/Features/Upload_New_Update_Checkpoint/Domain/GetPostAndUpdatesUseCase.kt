package com.example.influencer.Features.Upload_New_Update_Checkpoint.Domain

import com.example.influencer.Core.Data.Repositories.PostRepository.PostRepository
import com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model.Post
import com.example.influencer.Features.Upload_New_Update_Checkpoint.Data.FirestoreUpdatesRepository
import com.example.influencer.Features.Upload_New_Update_Checkpoint.Domain.Model.CheckPoint_Update_Item
import javax.inject.Inject

class GetPostAndUpdatesUseCase @Inject constructor(
    private val firestoreUpdatesRepository: FirestoreUpdatesRepository,
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(selectedCategory: String): Result<Pair<Post, List<CheckPoint_Update_Item>>> {
        return try {
            val postResult = firestoreUpdatesRepository.getLastPost(selectedCategory)
            if (postResult.isSuccess) {
                val post = postResult.getOrNull()!!
                val updates = postRepository.getPostUpdates(post.id)
                Result.success(Pair(post, updates ?: emptyList()))
            } else {
                Result.failure(postResult.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}