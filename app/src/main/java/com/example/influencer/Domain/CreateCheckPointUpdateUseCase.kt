package com.example.influencer.Domain

import com.example.influencer.Data.Repositories.UpdatesRepository.UpdatesRepository
import javax.inject.Inject

class CreateCheckPointUpdateUseCase @Inject constructor(
    private val updatesRepository:UpdatesRepository
    ) {
    suspend operator fun invoke(updateText: String){
        updatesRepository.createCheckPointUpdate(updateText)
    }
}