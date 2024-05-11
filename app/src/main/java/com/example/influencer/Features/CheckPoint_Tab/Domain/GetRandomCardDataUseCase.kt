package com.example.influencer.Features.CheckPoint_Tab.Domain

import com.example.influencer.Core.Data.Repositories.PostRepository.PostRepository
import com.example.influencer.Core.Data.Repositories.UserRepository.FirestoreUserRepository
import com.example.influencer.Features.CheckPoint_Tab.Data.CardsFilters
import com.example.influencer.Features.CheckPoint_Tab.Domain.Model.CardData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetRandomCardDataUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val cardsFilters: CardsFilters,
    private val firestoreUserRepository: FirestoreUserRepository
) {
    suspend operator fun invoke(categories: Set<String>):Result<CardData> = withContext(Dispatchers.IO){
        try {
            val postResult = cardsFilters.getRandomPost(categories)
            val post = postResult.getOrThrow()  // This will throw if the operation fails

            val userResult = firestoreUserRepository.getUserById(post.userId)
            val user = userResult.getOrThrow()

            val updates = postRepository.getPostUpdates(post.id)

            Result.success(CardData(user, post, updates))

        }catch (e: Exception){
            Result.failure(e)
        }
    }
}