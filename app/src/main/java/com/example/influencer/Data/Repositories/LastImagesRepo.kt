package com.example.influencer.Data.Repositories

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LastImagesRepo @Inject constructor(private val context: Context) {
    suspend fun getLastThreeImagesUri(): List<Uri> = withContext(Dispatchers.IO) {
        val imagesUriList = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        // The sortOrder does not include the LIMIT clause. Instead, sort in descending order.
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        // Use a selection that matches all rows. No actual filtering is done here.
        val selection = null
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

}