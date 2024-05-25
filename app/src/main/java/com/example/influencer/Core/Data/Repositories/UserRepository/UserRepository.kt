package com.example.influencer.Core.Data.Repositories.UserRepository

import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin

interface UserRepository {
    suspend fun getUserProfilePictureUrl(): String
    suspend fun saveUserAgeMonths(age:Int,months:Int)
    suspend fun saveUserCountry(countryName:String,countryFlag:String)
    suspend fun getUserById(userId: String?): Result<UsuarioSignin>
    suspend fun getCurrentUserId():String
    suspend fun isFollowing(currentUserId: String, targetUserId: String): Boolean
    suspend fun followUser(targetUserId: String)
    suspend fun unfollowUser(targetUserId: String)
}