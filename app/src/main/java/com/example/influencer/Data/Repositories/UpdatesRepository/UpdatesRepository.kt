package com.example.influencer.Data.Repositories.UpdatesRepository

import com.example.influencer.UI.Upload_New_Update_Checkpoint.Model.CheckPoint_Update_Item

interface UpdatesRepository {
    suspend fun createCheckPointUpdate(updateText:String)
    suspend fun getNextUpdateNumber(selectedCategory: String): Int
    suspend fun getPostUpdates(postId: String):Result<List<CheckPoint_Update_Item>>
}