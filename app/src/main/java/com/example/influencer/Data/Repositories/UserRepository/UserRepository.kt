package com.example.influencer.Data.Repositories.UserRepository

import com.example.influencer.UI.SignIn.Model.UsuarioSignin

interface UserRepository {
    suspend fun getUserProfilePictureUrl(): String
    suspend fun saveUserAgeMonths(age:Int,months:Int)
    suspend fun saveUserCountry(countryName:String,countryFlag:String)
    suspend fun getRandomUserDocument():Result<UsuarioSignin>
}