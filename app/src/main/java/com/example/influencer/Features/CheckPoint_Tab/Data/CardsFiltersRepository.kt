package com.example.influencer.Features.CheckPoint_Tab.Data

import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin
import com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model.Post
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CardsFiltersRepository @Inject constructor(
    private val db: FirebaseFirestore
):CardsFilters {

    override suspend fun getRandomPost(categories: Set<String>): Result<Post> = withContext(Dispatchers.IO) {
        try {
            val knownCategories = listOf("Love", "Family", "Friends", "Mental Health", "Work/Carrer", "Creativity", "Education/Learning", "Health/Fitness", "Hobbies/Interests")
            val tasks = mutableListOf<Task<QuerySnapshot>>()

            if ("Others" in categories) {
                val specificCategories = categories - "Others"
                tasks.add(db.collection("Posts").whereNotIn("selectedCategory", knownCategories).get())
                if (specificCategories.isNotEmpty()) {
                    tasks.add(db.collection("Posts").whereIn("selectedCategory", specificCategories.toList()).get())
                }
            } else {
                tasks.add(db.collection("Posts").whereIn("selectedCategory", categories.toList()).get())
            }

            // Await all tasks and combine results
            val results = Tasks.whenAllSuccess<QuerySnapshot>(tasks).await().flatMap { it.documents }

            // Apply randomness to select one document snapshot from the filtered results
            if (results.isNotEmpty()) {
                val randomDocumentSnapshot = results.random()
                val post = randomDocumentSnapshot.toObject(Post::class.java)
                post?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Failed to parse post document"))
            } else {
                Result.failure(Exception("No posts found matching criteria"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}