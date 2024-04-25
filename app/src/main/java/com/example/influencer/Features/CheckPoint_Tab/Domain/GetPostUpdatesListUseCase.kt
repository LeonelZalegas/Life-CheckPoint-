package com.example.influencer.Features.CheckPoint_Tab.Domain

import com.example.influencer.LaNuevaEstr.Core.Data.Repositories.PostRepository.PostRepository
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPostUpdatesListUseCase @Inject constructor(private val repository: PostRepository) {
    suspend operator fun invoke(postId: String, postOwnerId: String):SortedMap<Int, String>? {
       return repository.getPostUpdates(postId,postOwnerId)
    }
}