package com.example.influencer.Features.Settings.Data

import android.net.Uri

interface SettingsRepository {
    suspend fun updateUsername(newUsername: String)
    suspend fun updateProfilePicture(imageUri: Uri)
}