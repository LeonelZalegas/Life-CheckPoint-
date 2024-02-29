package com.example.influencer.Domain

import com.example.influencer.Data.Repositories.PostRepository.PostRepository
import com.example.influencer.Data.Repositories.UpdatesRepository.UpdatesRepository
import com.example.influencer.Data.Repositories.UserRepository.UserRepository
import com.example.influencer.UI.CheckPoint_Tab.Model.CardData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetRandomCardDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val updatesRepository: UpdatesRepository
) {
    suspend operator fun invoke():Result<CardData> = withContext(Dispatchers.IO){
        try {
            val userResult = userRepository.getRandomUserDocument()
            val user = userResult.getOrThrow() // This will throw if the operation fails

            val postResult = postRepository.getRandomPostFromUser(user.id)
            val post = postResult.getOrThrow()

            val updatesResult = updatesRepository.getPostUpdates(post.id)
            val updates = updatesResult.getOrDefault(emptyList()) // Use an empty list if the operation fails

            Result.success(CardData(user, post, updates))

        }catch (e: Exception){
            Result.failure(e)
        }
    }
}