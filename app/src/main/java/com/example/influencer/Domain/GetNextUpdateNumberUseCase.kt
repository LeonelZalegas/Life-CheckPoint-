package com.example.influencer.Domain

import com.example.influencer.Data.Repositories.UpdatesRepository.UpdatesRepository
import javax.inject.Inject

class GetNextUpdateNumberUseCase @Inject constructor(
    private val updateRepository:UpdatesRepository
) {
    suspend operator fun invoke(selectedCategory: String): Int {
        return updateRepository.getNextUpdateNumber(selectedCategory)
    }
}