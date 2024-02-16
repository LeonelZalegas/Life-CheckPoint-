package com.example.influencer.Domain.Upload_New_CheckpointUSECASES

import android.net.Uri
import com.example.influencer.Data.Repositories.LastImagesRepo
import javax.inject.Inject

class GetLastThreeImagesUseCase @Inject constructor(private val lastImagesRepo: LastImagesRepo) {
    suspend operator fun invoke(): List<Uri>{
        return lastImagesRepo.getLastThreeImagesUri()
    }
}