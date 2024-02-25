package com.example.influencer.Data.Repositories.UserRepository

interface UserRepository {
    suspend fun getUserProfilePictureUrl(): String
}