package com.example.influencer.Features.CheckPoint_Tab.Data

import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin
import com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model.Post
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CardsFiltersRepository @Inject constructor(
    private val db: FirebaseFirestore
):CardsFilters {

    // Fetch a random post from the global posts collection
    override suspend fun getRandomPost(): Result<Post> = withContext(Dispatchers.IO) {
        try {
            val postsSnapshot = db.collection("Posts").get().await()
            if (postsSnapshot.documents.isNotEmpty()) {
                val randomPostDoc = postsSnapshot.documents.random()
                val post = randomPostDoc.toObject(Post::class.java)   //en data class Post hacemos @DocumentId en el ID para no tener que hacer (Post::class.java)?.apply {this.id = randomPostDoc.id}
                post?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Failed to parse post document"))
            } else {
                Result.failure(Exception("No posts found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}