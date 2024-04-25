package com.example.influencer.Features.Upload_New_Checkpoint.Domain

import android.net.Uri
import com.example.influencer.LaNuevaEstr.Features.Upload_New_Checkpoint.Data.LastImagesRepo
import javax.inject.Inject

class GetLastThreeImagesUseCase @Inject constructor(private val lastImagesRepo: LastImagesRepo) {
    suspend operator fun invoke(): List<Uri>{
        return lastImagesRepo.getLastThreeImagesUri()
    }
}