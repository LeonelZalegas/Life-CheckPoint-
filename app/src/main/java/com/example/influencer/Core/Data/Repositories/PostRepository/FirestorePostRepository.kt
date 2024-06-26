package com.example.influencer.Core.Data.Repositories.PostRepository

import android.util.Log
import com.example.influencer.Core.Data.Network.AuthenticationService
import com.example.influencer.Core.Utils.CheckpointsCategoriesList
import com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model.Post
import com.example.influencer.Features.Upload_New_Update_Checkpoint.Domain.Model.CheckPoint_Update_Item
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import javax.inject.Singleton
//en este Repo vamos a poner TOD0 (guardar o traer (retrieve) Datos relacionados con Posts/Checkpoints unicamnete, ligados a un usuario o no)

// Concrete implementation of PostRepository using Firebase Firestore
@Singleton
class FirestorePostRepository @Inject constructor(
    private val db:FirebaseFirestore,
    private val authService: AuthenticationService,
    private val checkpointsCategoriesList: CheckpointsCategoriesList
): PostRepository {

    private suspend fun getUserPostCategories(): List<String> = withContext(Dispatchers.IO) {
        val uid = authService.uid
        val postsCollectionRef = db.collection("Posts")

        try {
            val querySnapshot = postsCollectionRef
                .whereEqualTo("userId", uid)
                .get()
                .await()
            return@withContext querySnapshot.documents.mapNotNull { document ->
                document.getString("selectedCategory")
            }.distinct()
        } catch (e: Exception) {
            Log.e("FirestorePostRepository", "Error fetching user post categories", e)
            return@withContext emptyList<String>()
        }
    }

    //Para mostrar los Rows de las categorias de los posts hechas x usuario actualmente en los Updates
   //https://www.notion.so/Upload-Checkpoint-1c875423235f4180a588c8453a7140e3?pvs=4#77ae0184119544a59e2374b179122ade
    fun getUserPostCategoriesFuture(): CompletableFuture<List<String>> {
        val future = CompletableFuture<List<String>>()  //es como 1 variable pero q es async, no se queda colgada esperando el valor q una funcion le esta por asignar, sino que sabe el tipo de valor nomas y espera y no interrumpe el thread
        GlobalScope.launch {     // es una corrutina  pero no esta ligada al lifecycle de nada (por ej no se termina si Viewmodel termina), esta termina cuando el task termina nomas
            try {
                val categories = getUserPostCategories()
                future.complete(categories)
            } catch (e: Exception) {
                future.completeExceptionally(e)
            }
        }
        return future
    }

    //modifica la BD en tiempo real sumando 1 al contador de likes y aparte devuelve el nuevo valor actualizado
    override suspend fun likePost(postId: String): Int = withContext(Dispatchers.IO) {
        val likedPostRef = db.collection("Posts").document(postId)
        val currentUserLikedPostsRef = db.collection("Usuarios").document(authService.uid).collection("LikedPosts")
        val likersRef = likedPostRef.collection("Likers").document(authService.uid)

        // Set the likedTime when adding the user to the Likers subcollection
        likersRef.set(mapOf("likedTime" to FieldValue.serverTimestamp())).await()

        //referecia al usuario que esta usando su cuenta actualente en la app y bueno..aca tmb se crea la coleccion LikedPosts de ese usuario
        currentUserLikedPostsRef.document(postId).set(mapOf("likedTime" to FieldValue.serverTimestamp())).await()

        // Perform a transaction to increment the likes count
        val newLikesCount = db.runTransaction { transaction ->
            val snapshot = transaction.get(likedPostRef)
            val currentLikes = snapshot.getLong("likes") ?: 0
            val newLikes = currentLikes + 1
            transaction.update(likedPostRef, "likes", newLikes)
            newLikes // Return the new likes count
        }.await()

        return@withContext newLikesCount.toInt()
    }

    // same que likePost pero hace -1 al contador de likes
    override suspend fun unlikePost(postId: String): Int = withContext(Dispatchers.IO) {
        val likedPostRef = db.collection("Posts").document(postId)
        val currentUserLikedPostsRef = db.collection("Usuarios").document(authService.uid).collection("LikedPosts")
        val likersRef = likedPostRef.collection("Likers").document(authService.uid)

        // Remove the user from the Likers subcollection
        likersRef.delete().await()

        currentUserLikedPostsRef.document(postId).delete().await()

        val newLikesCount = db.runTransaction { transaction ->
            val snapshot = transaction.get(likedPostRef)
            val currentLikes = snapshot.getLong("likes") ?: 0
            val newLikes = if (currentLikes > 0) currentLikes - 1 else 0 // Prevent negative likes
            transaction.update(likedPostRef, "likes", newLikes)
            newLikes
        }.await()

        return@withContext newLikesCount.toInt()
    }

    override suspend fun isPostLiked(postId: String): Boolean = withContext(Dispatchers.IO) {
        val uid = authService.uid
        val userDocRef = db.collection("Usuarios").document(uid)
        val likedPostsRef = userDocRef.collection("LikedPosts")
        val document = likedPostsRef.document(postId).get().await()
        return@withContext document.exists()
    }

    override suspend fun getPostUpdates(postId: String): List<CheckPoint_Update_Item>? = withContext(Dispatchers.IO) {
        val updatesList = mutableListOf<CheckPoint_Update_Item>()
        try {
            // Access the post using postId
            val updatesSnapshot = db.collection("Posts")
                .document(postId)
                .collection("Updates")
                .orderBy("update_Number", Query.Direction.ASCENDING)
                .get()
                .await()


            Log.d("FirestoreDebug", "Updates found: ${updatesSnapshot.size()}")
            updatesSnapshot.documents.forEach { document ->
                Log.d("FirestoreDebug", "Update: ${document.id} => ${document.data}")
            }

//             Check if the "Updates" collection is empty or does not exist
            if (updatesSnapshot.isEmpty) {
                return@withContext null
            }

            for (document in updatesSnapshot.documents) {
                val updateNumber = document.getLong("update_Number")?.toInt()
                val updateText = document.getString("update_Text")
                if (updateNumber != null && updateText != null) {
                    updatesList.add(CheckPoint_Update_Item(updateNumber, updateText))
                }
            }
        } catch (e: Exception) {
            Log.e("FirestorePostRepository", "Error fetching post updates", e)
            return@withContext null
        }
        return@withContext updatesList.sortedBy { it.update_Number }
    }

    override suspend fun getUserPostsByCategory(userId: String, category: String): List<Post>? = withContext(Dispatchers.IO) {
        try {
            val postsQuery = db.collection("Posts")
                .whereEqualTo("userId", userId)

            val query = if (category == "Others") {
                val knownCategories = checkpointsCategoriesList.categories.map { it.text }
                postsQuery
                    .whereNotIn("selectedCategory", knownCategories)
                    .orderBy("selectedCategory") // Required for whereNotIn to work efficiently (si o si firebase te pide esto aunque logicamente no sea necesario)
            } else {
                postsQuery.whereEqualTo("selectedCategory", category)
            }

            val querySnapshot = query
                .orderBy("creationDate", Query.Direction.DESCENDING) // Common ordering for all queries
                .get().await()

            if (querySnapshot.isEmpty) {
                return@withContext null
            }

            return@withContext querySnapshot.documents.mapNotNull { document ->
                document.toObject(Post::class.java)
            }
        } catch (e: Exception) {
            Log.e("FirestorePostRepository", "Error fetching user posts by category", e)
            return@withContext null
        }
    }

    override suspend fun getUserLikedPosts(userId: String): List<Post>? = withContext(Dispatchers.IO) {
        try {
            val likedPostsSnapshot = db.collection("Usuarios")
                .document(userId)
                .collection("LikedPosts")
                .orderBy("likedTime", Query.Direction.DESCENDING)
                .get()
                .await()

            if (likedPostsSnapshot.isEmpty) {
                return@withContext null
            }

            val likedPostIds = likedPostsSnapshot.documents.map { it.id }

            if (likedPostIds.isEmpty()) {
                return@withContext null
            }

            val postsSnapshot = db.collection("Posts")
                .whereIn(FieldPath.documentId(), likedPostIds)
                .get()
                .await()

            return@withContext postsSnapshot.documents.mapNotNull { document ->
                document.toObject(Post::class.java)
            }
        } catch (e: Exception) {
            Log.e("FirestorePostRepository", "Error fetching user liked posts", e)
            return@withContext null
        }
    }

    override suspend fun deletePost(postId: String): Unit = withContext(Dispatchers.IO) {
        val postRef = db.collection("Posts").document(postId)
        val userRef = db.collection("Usuarios").document(authService.uid)
        val storage = FirebaseStorage.getInstance()

        // Retrieve the post to get image URLs before deleting
        val postSnapshot = postRef.get().await()
        val image1 = postSnapshot.getString("image_1")
        val image2 = postSnapshot.getString("image_2")

        // Get all users who liked the post
        val likersRef = postRef.collection("Likers")
        val likersSnapshot = likersRef.get().await()
        val userIds = likersSnapshot.documents.map { it.id }

        // Start a batch to delete all related data
        val batch = db.batch()
        batch.delete(postRef)

        // Delete the liked post entries in each user's profile
        userIds.forEach { userId ->
            val likedPostRef = db.collection("Usuarios").document(userId).collection("LikedPosts").document(postId)
            batch.delete(likedPostRef)
        }

        // Delete each document in the "Likers" subcollection
        likersSnapshot.documents.forEach { doc ->
            batch.delete(likersRef.document(doc.id))
        }

        // Decrement the post count in the user's document
        batch.update(userRef, "postCount", FieldValue.increment(-1))

        // Commit the batch and proceed with deleting images if successful
        try {
            batch.commit().await()

            // Delete images from Firebase Storage if they exist and batch was successful
            image1?.let {
                val imageRef = storage.getReferenceFromUrl(it)
                imageRef.delete().await()
            }

            image2?.let {
                val imageRef = storage.getReferenceFromUrl(it)
                imageRef.delete().await()
            }

        } catch (e: Exception) {
            throw Exception("Failed to delete post and related data: ${e.message}")
        }
    }

    override suspend fun getPost(postId: String): Result<Post> = withContext(Dispatchers.IO) {
        try {
            val documentSnapshot = db.collection("Posts").document(postId).get().await()
            val post = documentSnapshot.toObject(Post::class.java)
            if (post != null) {
                Result.success(post)
            } else {
                Result.failure(Exception("Post not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}