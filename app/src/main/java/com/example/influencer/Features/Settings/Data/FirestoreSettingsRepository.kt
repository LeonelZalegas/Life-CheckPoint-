package com.example.influencer.Features.Settings.Data

import android.net.Uri
import android.util.Log
import com.example.influencer.Core.Data.Network.AuthenticationService
import com.example.influencer.Core.Data.Preferences.UserPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreSettingsRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val authService: AuthenticationService,
    private val userPreferences: UserPreferences
): SettingsRepository {

    override suspend fun updateUsername(newUsername: String): Unit = withContext(Dispatchers.IO) {
        try {
            val userId = authService.uid
            val userDocRef = db.collection("Usuarios").document(userId)
            userDocRef.update("username", newUsername).await()
        } catch (e: Exception) {
            Log.e("FirestoreSettingsRepository", "Error updating username", e)
        }
    }

    override suspend fun updateProfilePicture(imageUri: Uri) = withContext(Dispatchers.IO) {
        val userId = authService.uid ?: return@withContext
        val storageRef = FirebaseStorage.getInstance().reference.child("images_profile/$userId.jpg")
        val userDocRef = db.collection("Usuarios").document(userId)

        // Get the current profile picture URL
        val currentProfileUrl = userDocRef.get().await().getString("profilePictureUrl")

        // Upload the new image to Firebase Storage
        storageRef.putFile(imageUri).await().storage.downloadUrl.await().toString().let { newUrl ->
            // Update the Firestore document
            userDocRef.update("profilePictureUrl", newUrl).await()

            // Delete the old image if it's in the 'images_profile' folder and not a Google profile link
            if (currentProfileUrl != null && currentProfileUrl.startsWith("https://firebasestorage.googleapis.com/v0/b/") && currentProfileUrl.contains("/images_profile/")) {
                val oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentProfileUrl)
                oldImageRef.delete().await()
            }
        }
    }

    override suspend fun logoutUser() = withContext(Dispatchers.IO) {
        try {
            authService.signOut()
            userPreferences.isSignedInSync = false
        } catch (e: Exception) {
            throw e
        }
    }
}
