package com.example.influencer.Features.General_Individual_Checkpoint.Domain

import com.example.influencer.Core.Data.Repositories.PostRepository.PostRepository
import com.example.influencer.Core.Data.Repositories.UserRepository.FirestoreUserRepository
import com.example.influencer.Features.CheckPoint_Tab.Domain.Model.CardData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCardDataUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val firestoreUserRepository: FirestoreUserRepository
) {
    suspend operator fun invoke(userId: String, postId: String):Result<CardData> = withContext(Dispatchers.IO){
        try {
            val postResult = postRepository.getPost(postId)
            val post = postResult.getOrThrow()

            val userResult = firestoreUserRepository.getUserById(userId)
            val user = userResult.getOrThrow()

            val updates = postRepository.getPostUpdates(postId)

            Result.success(CardData(user, post, updates))

        }catch (e: Exception){
            Result.failure(e)
        }
    }
}