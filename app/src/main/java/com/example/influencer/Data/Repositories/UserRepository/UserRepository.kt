package com.example.influencer.Data.Repositories.UserRepository

interface UserRepository {
    suspend fun getUserProfilePictureUrl(): String
    suspend fun saveUserAgeMonths(age:Int,months:Int)
    suspend fun saveUserCountry(countryName:String,countryFlag:Int)
}