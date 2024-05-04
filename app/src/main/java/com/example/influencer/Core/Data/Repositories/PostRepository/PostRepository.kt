package com.example.influencer.Core.Data.Repositories.PostRepository


import com.example.influencer.Features.Upload_New_Update_Checkpoint.Domain.Model.CheckPoint_Update_Item

// Interface defining the operations that a PostRepository should support
interface PostRepository {
    suspend fun likePost(postId: String):Int
    suspend fun unlikePost(postId: String):Int
    suspend fun isPostLiked (postId: String):Boolean
    suspend fun getPostUpdates(postId: String): List<CheckPoint_Update_Item>?
}