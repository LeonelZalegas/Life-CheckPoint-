package com.example.influencer.Core.Data.Repositories.PostRepository

import com.example.influencer.LaNuevaEstr.Features.Upload_New_Checkpoint.Domain.Model.Post
import com.example.influencer.LaNuevaEstr.Features.Upload_New_Update_Checkpoint.Domain.Model.CheckPoint_Update_Item
import java.util.*

// Interface defining the operations that a PostRepository should support
interface PostRepository {
    suspend fun savePost(post: Post)
    suspend fun getRandomPostFromUser(userId: String): Result<Post>
    suspend fun likePost(postId: String,postOwnerId: String):Int
    suspend fun unlikePost(postId: String,postOwnerId: String):Int
    suspend fun isPostLiked (postId: String):Boolean
    suspend fun getPostUpdates(postOwnerId: String, postId: String): SortedMap<Int, String>?
}