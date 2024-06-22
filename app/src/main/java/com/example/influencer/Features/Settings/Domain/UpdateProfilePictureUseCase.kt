package com.example.influencer.Features.Settings.Domain

import android.net.Uri
import com.example.influencer.Features.Settings.Data.FirestoreSettingsRepository
import com.example.influencer.Features.Settings.Data.SettingsRepository
import javax.inject.Inject

class UpdateProfilePictureUseCase  @Inject constructor(
    private val settingsRepository: SettingsRepository
    ) {
    suspend operator fun invoke(imageUri: Uri) {
        settingsRepository.updateProfilePicture(imageUri)
    }
}