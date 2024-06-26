package com.example.influencer.Features.Upload_New_Checkpoint.Data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.influencer.Core.Data.Network.AuthenticationService
import com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model.Post
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

//https://www.notion.so/Upload-Checkpoint-1c875423235f4180a588c8453a7140e3?pvs=4#5a44c86997f14ac888d595e96d46743b
@Singleton
class UploadCheckpointRepo @Inject constructor(
    private val context: Context,
    private val db: FirebaseFirestore,
    private val authService: AuthenticationService
): UploadCheckpoint {

    override suspend fun getLastThreeImagesUri(): List<Uri> = withContext(Dispatchers.IO) {
        val imagesUriList = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"   // The sortOrder does not include the LIMIT clause. Instead, sort in descending order.
        val selection = null   // Use a selection that matches all rows. No actual filtering is done here.
        val selectionArgs = null

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            // Instead of using the LIMIT clause, manually control the number of iterations.
            var count = 0
            while (cursor.moveToNext() && count < 3) { // Only iterate over the first 3 rows
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                imagesUriList.add(contentUri)
                count++
            }
        }
        imagesUriList
    }

    override suspend fun savePost(post: Post): Unit = withContext(Dispatchers.IO) {
        val uid = authService.uid
        post.userId = uid  // Set the userId of the post

        val globalPostsRef = db.collection("Posts")
        try {
            // Fetch posts with the same selectedCategory as the new post
            val sameCategoryPosts = globalPostsRef
                .whereEqualTo("userId", uid)  // Ensure we're only looking at the user's own posts
                .whereEqualTo("selectedCategory", post.selectedCategory)
                .get()
                .await()

            // Find the highest checkpointCategoryCounter among them
            var highestCounter = sameCategoryPosts.documents
                .maxOfOrNull { it.getLong("checkpointCategoryCounter") ?: 0L }
                ?: 0L // Default to 0 if no posts found

            // Increment the highest counter by 1 for the new post
            post.checkpointCategoryCounter = (highestCounter + 1).toInt()

            // Now, save the new post with the updated checkpointCategoryCounter
            globalPostsRef.document().set(post).await()

            // Atomically increment the postCount field of the user document
            val userDocRef = db.collection("Usuarios").document(uid)
            userDocRef.update("postCount", FieldValue.increment(1)).await()

        } catch (e: Exception) {
            // Handle possible exceptions, e.g., logging or notifying the user
            throw e
        }
    }

}