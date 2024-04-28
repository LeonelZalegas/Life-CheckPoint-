package com.example.influencer.Features.Upload_New_Update_Checkpoint.Domain

import com.example.influencer.Features.Upload_New_Update_Checkpoint.Data.UpdatesRepository
import javax.inject.Inject

class GetNextUpdateNumberUseCase @Inject constructor(
    private val updateRepository: UpdatesRepository
) {
    suspend operator fun invoke(selectedCategory: String): Int {
        return updateRepository.getNextUpdateNumber(selectedCategory)
    }
}