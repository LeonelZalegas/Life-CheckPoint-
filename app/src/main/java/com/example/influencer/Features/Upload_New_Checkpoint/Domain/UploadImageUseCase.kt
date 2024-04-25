package com.example.influencer.Features.Upload_New_Checkpoint.Domain

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

// Use case for uploading an image
@Singleton
class UploadImageUseCase @Inject constructor(private val storage: FirebaseStorage) {
    suspend operator fun invoke(imageUri: Uri):String{
        return withContext(Dispatchers.IO){
            val ref = storage.reference.child("images_Posts/${UUID.randomUUID()}")
            ref.putFile(imageUri).await()
            ref.downloadUrl.await().toString()
        }
    }
}