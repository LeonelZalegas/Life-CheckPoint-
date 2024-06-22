package com.example.influencer.Features.Settings.Domain

import com.example.influencer.Features.Settings.Data.SettingsRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke() = settingsRepository.logoutUser()
}
