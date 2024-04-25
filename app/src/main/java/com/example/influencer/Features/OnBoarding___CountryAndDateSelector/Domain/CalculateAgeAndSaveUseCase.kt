package com.example.influencer.Features.OnBoarding___CountryAndDateSelector.Domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.influencer.LaNuevaEstr.Core.Data.Repositories.UserRepository.UserRepository
import com.example.influencer.LaNuevaEstr.Features.Upload_New_Checkpoint.Domain.Model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalculateAgeAndSaveUseCase @Inject constructor(private val userRepository: UserRepository){

    suspend operator fun invoke(selectedDateMillis: Long) = withContext(Dispatchers.IO){
        val dob = Instant.ofEpochMilli(selectedDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        val now = LocalDate.now()
        val period = Period.between(dob, now)

        val years = period.years
        val months = period.months
        userRepository.saveUserAgeMonths(years,months)
    }
}