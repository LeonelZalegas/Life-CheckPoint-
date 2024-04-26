package com.example.influencer.Features.CheckPoint_Tab.Domain

import com.example.influencer.Core.Data.Repositories.PostRepository.PostRepository
import com.example.influencer.Core.Data.Repositories.UpdatesRepository.UpdatesRepository
import com.example.influencer.Core.Data.Repositories.UserRepository.UserRepository
import com.example.influencer.Features.CheckPoint_Tab.Domain.Model.CardData
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

            val updates = postRepository.getPostUpdates(post.id,user.id)

            Result.success(CardData(user, post, updates))

        }catch (e: Exception){
            Result.failure(e)
        }
    }
}