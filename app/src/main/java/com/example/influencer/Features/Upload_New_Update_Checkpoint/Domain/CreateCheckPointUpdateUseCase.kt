package com.example.influencer.Features.Upload_New_Update_Checkpoint.Domain

import com.example.influencer.LaNuevaEstr.Core.Data.Repositories.UpdatesRepository.UpdatesRepository
import javax.inject.Inject

class CreateCheckPointUpdateUseCase @Inject constructor(
    private val updatesRepository: UpdatesRepository
    ) {
    suspend operator fun invoke(updateText: String){
        updatesRepository.createCheckPointUpdate(updateText)
    }
}