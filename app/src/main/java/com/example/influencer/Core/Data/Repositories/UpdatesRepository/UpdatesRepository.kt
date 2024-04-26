package com.example.influencer.Core.Data.Repositories.UpdatesRepository

import com.example.influencer.Features.Upload_New_Update_Checkpoint.Domain.Model.CheckPoint_Update_Item


interface UpdatesRepository {
    suspend fun createCheckPointUpdate(updateText:String)
    suspend fun getNextUpdateNumber(selectedCategory: String): Int
}