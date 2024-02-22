package com.example.influencer.Data.Repositories.PostRepository

import com.example.influencer.UI.Upload_New_Checkpoint.Model.Post

// Interface defining the operations that a PostRepository should support
interface PostRepository {
    suspend fun savePost(post: Post)
}