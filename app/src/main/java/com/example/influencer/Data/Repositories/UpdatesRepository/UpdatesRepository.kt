package com.example.influencer.Data.Repositories.UpdatesRepository

interface UpdatesRepository {
    suspend fun createCheckPointUpdate(updateText:String)
    suspend fun getNextUpdateNumber(selectedCategory: String): Int
}