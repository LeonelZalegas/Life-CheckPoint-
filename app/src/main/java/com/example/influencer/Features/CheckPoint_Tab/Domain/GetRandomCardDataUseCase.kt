package com.example.influencer.Features.CheckPoint_Tab.Domain

import com.example.influencer.Core.Data.Repositories.PostRepository.PostRepository
import com.example.influencer.Features.CheckPoint_Tab.Data.CardsFilters
import com.example.influencer.Features.CheckPoint_Tab.Domain.Model.CardData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetRandomCardDataUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val cardsFilters: CardsFilters
) {
    suspend operator fun invoke():Result<CardData> = withContext(Dispatchers.IO){
        try {
            val userResult = cardsFilters.getRandomUserDocument()
            val user = userResult.getOrThrow() // This will throw if the operation fails

            val postResult = cardsFilters.getRandomPostFromUser(user.id)
            val post = postResult.getOrThrow()

            val updates = postRepository.getPostUpdates(post.id,user.id)

            Result.success(CardData(user, post, updates))

        }catch (e: Exception){
            Result.failure(e)
        }
    }
}