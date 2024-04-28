package com.example.influencer.Features.Upload_New_Checkpoint.Data

import android.net.Uri
import com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model.Post

interface UploadCheckpoint {
    suspend fun getLastThreeImagesUri(): List<Uri>
    suspend fun savePost(post: Post)
}