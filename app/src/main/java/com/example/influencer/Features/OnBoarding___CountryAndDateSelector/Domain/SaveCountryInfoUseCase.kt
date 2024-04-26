package com.example.influencer.Features.OnBoarding___CountryAndDateSelector.Domain

import com.example.influencer.Core.Data.Repositories.UserRepository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveCountryInfoUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(countryName: String, countryFlag: String) = withContext(Dispatchers.IO){
        userRepository.saveUserCountry(countryName,countryFlag)
    }
}