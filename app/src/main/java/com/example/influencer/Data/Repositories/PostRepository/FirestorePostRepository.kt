package com.example.influencer.Data.Repositories.PostRepository

import android.util.Log
import com.example.influencer.Data.Network.AuthenticationService
import com.example.influencer.UI.Upload_New_Checkpoint.Model.Post
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import javax.inject.Singleton

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
        postsCollectionRef.document().set(post).await()
    }

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
}