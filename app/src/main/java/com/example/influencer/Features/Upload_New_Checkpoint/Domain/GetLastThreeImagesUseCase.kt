package com.example.influencer.Features.Upload_New_Checkpoint.Domain

import android.net.Uri
import com.example.influencer.Features.Upload_New_Checkpoint.Data.UploadCheckpoint
import javax.inject.Inject

class GetLastThreeImagesUseCase @Inject constructor(private val uploadCheckpoint: UploadCheckpoint) {
    suspend operator fun invoke(): List<Uri>{
        return uploadCheckpoint.getLastThreeImagesUri()
    }
}