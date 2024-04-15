package com.example.influencer.Domain

import com.example.influencer.Data.Repositories.PostRepository.PostRepository
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPostUpdatesListUseCase @Inject constructor(private val repository: PostRepository) {
    suspend operator fun invoke(postId: String, postOwnerId: String):SortedMap<Int, String>? {
       return repository.getPostUpdates(postId,postOwnerId)
    }
}