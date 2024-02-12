package com.example.influencer.Data.Repositories.FirestorePostRepository

import com.example.influencer.UI.Upload_New_Checkpoint.Model.Post
import com.google.android.gms.tasks.Task

// Interface defining the operations that a PostRepository should support
interface PostRepository {
    suspend fun savePost(post: Post)
}