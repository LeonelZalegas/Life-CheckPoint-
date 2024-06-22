package com.example.influencer.Features.Settings.Domain

import com.example.influencer.Features.Settings.Data.SettingsRepository
import javax.inject.Inject

class SaveNewUsernameUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(newUsername: String) {
        settingsRepository.updateUsername(newUsername)
    }

}