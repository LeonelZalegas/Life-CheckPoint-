package com.example.influencer.Core.Data.Repositories.PostRepository

import android.util.Log
import com.example.influencer.Core.Data.Network.AuthenticationService
import com.example.influencer.LaNuevaEstr.Features.Upload_New_Checkpoint.Domain.Model.Post
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import javax.inject.Singleton
//en este Repo vamos a poner TOD0 (guardar o traer (retrieve) Datos relacionados con Posts/Checkpoints unicamnete, ligados a un usuario o no)

// Concrete implementation of PostRepository using Firebase Firestore
@Singleton
class FirestorePostRepository @Inject constructor(
    private val db:FirebaseFirestore,
    private val authService: _root_ide_package_.com.example.influencer.Core.Data.Network.AuthenticationService
): _root_ide_package_.com.example.influencer.Core.Data.Repositories.PostRepository.PostRepository {

    override suspend fun savePost(post: Post): Unit = withContext(Dispatchers.IO){
        val uid = authService.getUid()

        val userDocRef = db.collection("Usuarios").document(uid)
        val postsCollectionRef = userDocRef.collection("Posts")
        try {
            // Fetch posts with the same selectedCategory as the new post
            val sameCategoryPosts = postsCollectionRef
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
            postsCollectionRef.document().set(post).await()

            // Atomically increment the postCount field of the user document
            userDocRef.update("postCount", FieldValue.increment(1)).await()
        } catch (e: Exception) {
            // Handle possible exceptions, e.g., logging or notifying the user
            throw e
        }
    }

    //TODO agregar el metodo de abajo a la interfaz PostRepository
    suspend fun getUserPostCategories(): List<String> = withContext(Dispatchers.IO) {
        val uid = authService.getUid()
        val userDocRef = db.collection("Usuarios").document(uid)
        val postsCollectionRef = userDocRef.collection("Posts")

        try {
            val querySnapshot = postsCollectionRef.get().await()
            return@withContext querySnapshot.documents.mapNotNull { document ->
                document.getString("selectedCategory")    //el mapNotNull automaticamente crea una lista y va guardando el resultado de lo que va modificando en cada docu y lo guarda en ella
            }.distinct()
        } catch (e: Exception) {
            Log.e("FirestorePostRepository", "Error fetching user post categories", e)
            return@withContext emptyList<String>()
        }
    }

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

    override suspend fun getRandomPostFromUser(userId: String): Result<Post> = withContext(Dispatchers.IO){
        try {
           val postsSnapshot = db.collection("Usuarios")
            .document(userId)
            .collection("Posts")
            .get()
            .await()

          if (postsSnapshot.documents.isNotEmpty()) {
             val randomPostDoc = postsSnapshot.documents.random()
             val post = randomPostDoc.toObject(Post::class.java)  //en data class Post hacemos @DocumentId en el ID para no tener que hacer (Post::class.java)?.apply {this.id = randomPostDoc.id}
             post?.let {
                 Result.success(it)
             } ?: Result.failure(Exception("Failed to parse post document"))
          }  else {
            Result.failure(Exception("No posts found for user"))
          }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //modifica la BD en tiempo real sumando 1 al contador de likes y aparte devuelve el nuevo valor actualizado
    override suspend fun likePost(postId: String, postOwnerId: String): Int = withContext(Dispatchers.IO) {
        //referencia al usuario al cual cuyo post le dieron like
          val likedPostUserDocRef = db.collection("Usuarios").document(postOwnerId)
          val likedPostRef = likedPostUserDocRef.collection("Posts").document(postId)

        //referecia al usuario que esta usando su cuenta actualente en la app y bueno..aca tmb se crea la coleccion LikedPosts de ese usuario
          val currentUserlikedPostsRef = db.collection("Usuarios").document(authService.uid).collection("LikedPosts")

          currentUserlikedPostsRef.document(postId).set(mapOf("likedTime" to FieldValue.serverTimestamp())).await()

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
    override suspend fun unlikePost(postId: String, postOwnerId: String): Int = withContext(Dispatchers.IO) {
        val likedPostUserDocRef = db.collection("Usuarios").document(postOwnerId)
        val likedPostRef = likedPostUserDocRef.collection("Posts").document(postId)

        val currentUserlikedPostsRef = db.collection("Usuarios").document(authService.uid).collection("LikedPosts")

        currentUserlikedPostsRef.document(postId).delete().await()

        // Perform a transaction to decrement the likes count
        val newLikesCount = db.runTransaction { transaction ->
            val snapshot = transaction.get(likedPostRef)
            val currentLikes = snapshot.getLong("likes") ?: 0
            val newLikes = if (currentLikes > 0) currentLikes - 1 else 0 // Prevent negative likes
            transaction.update(likedPostRef, "likes", newLikes)
            newLikes // Return the new likes count
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

    override suspend fun getPostUpdates(postId: String,postOwnerId: String): SortedMap<Int, String>? = withContext(Dispatchers.IO) {
        val updatesMap = sortedMapOf<Int, String>()
        try {
            // Access the specific post using postOwnerId and postId
            val updatesSnapshot = db.collection("Usuarios")
                .document(postOwnerId)
                .collection("Posts")
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
                    updatesMap[updateNumber] = updateText
                }
            }
        } catch (e: Exception) {
            Log.e("FirestorePostRepository", "Error fetching post updates", e)
            return@withContext null
        }
        return@withContext updatesMap.takeIf { it.isNotEmpty() }
    }

}