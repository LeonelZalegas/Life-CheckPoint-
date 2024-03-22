package com.example.influencer.Data.Repositories.PostRepository

import android.util.Log
import com.example.influencer.Data.Network.AuthenticationService
import com.example.influencer.UI.Upload_New_Checkpoint.Model.Post
import com.example.influencer.UI.Upload_New_Update_Checkpoint.Model.CheckPoint_Update_Item
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
    private val authService: AuthenticationService
): PostRepository {

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

    override suspend fun likePost(postId: String, postOwnerId: String): Unit = withContext(Dispatchers.IO) {
        //referencia al usuario al cual cuyo post le dieron like
          val likedPostUserDocRef = db.collection("Usuarios").document(postOwnerId)
          val likedPostRef = likedPostUserDocRef.collection("Posts").document(postId)

        //referecia al usuario que esta usando su cuenta actualente en la app y bueno..aca tmb se crea la coleccion LikedPosts de ese usuario
          val currentUserlikedPostsRef = db.collection("Usuarios").document(authService.uid).collection("LikedPosts")

          currentUserlikedPostsRef.document(postId).set(mapOf("likedTime" to FieldValue.serverTimestamp())).await()
          likedPostRef.update("likes", FieldValue.increment(1)).await()
    }

    override suspend fun unlikePost(postId: String, postOwnerId: String): Unit = withContext(Dispatchers.IO) {
        val likedPostUserDocRef = db.collection("Usuarios").document(postOwnerId)
        val likedPostRef = likedPostUserDocRef.collection("Posts").document(postId)

        val currentUserlikedPostsRef = db.collection("Usuarios").document(authService.uid).collection("LikedPosts")

        currentUserlikedPostsRef.document(postId).delete().await()
        likedPostRef.update("likes", FieldValue.increment(-1)).await()
    }

    override suspend fun isPostLiked(postId: String): Boolean = withContext(Dispatchers.IO) {
        val uid = authService.uid
        val userDocRef = db.collection("Usuarios").document(uid)
        val likedPostsRef = userDocRef.collection("LikedPosts")
        val document = likedPostsRef.document(postId).get().await()
        return@withContext document.exists()
    }
}