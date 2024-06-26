package com.example.influencer.Features.Upload_New_Update_Checkpoint.Data

import com.example.influencer.Core.Data.Network.AuthenticationService
import com.example.influencer.Features.Upload_New_Checkpoint.Domain.Model.Post
import com.example.influencer.Features.Upload_New_Update_Checkpoint.Domain.Model.CheckPoint_Update_Item
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirestoreUpdatesRepository @Inject constructor(
    private val db:FirebaseFirestore,
    private val authService: AuthenticationService
): UpdatesRepository {

    //devuelve el Post con = categoria (seleccionada por user) y q ha sido la ultima publicada de esa categoria
    private suspend fun getLastPostDocumentRef(selectedCategory: String): DocumentReference = withContext(Dispatchers.IO) {
        val uid = authService.uid
        val postsCollectionRef = db.collection("Posts")

        return@withContext postsCollectionRef
            .whereEqualTo("userId", uid)
            .whereEqualTo("selectedCategory", selectedCategory)
            .orderBy("checkpointCategoryCounter", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()
            .documents
            .first()
            .reference
    }

    override suspend fun createCheckPointUpdate(updateText: String): Unit = withContext(Dispatchers.IO){
         val updatesCollectionRef = lastPostDocRefCache.collection("Updates")

         val updateDocument = CheckPoint_Update_Item(nextUpdateNumberCache, updateText)
         updatesCollectionRef.document().set(updateDocument).await()

        // Atomically increment the UpdatesAmount field in the Post document
        lastPostDocRefCache.update("UpdatesAmount", FieldValue.increment(1))
    }

    //se  fija si la coleccion "Updates" esta creada en el Post, si no lo esta envia un 1 (1er update del post), si ya esta creada, se fija el > valor de update_Number de todos los documentos y devuelve ese valor +1
    override suspend fun getNextUpdateNumber(selectedCategory: String): Int = withContext(Dispatchers.IO) {
        lastPostDocRefCache = getLastPostDocumentRef(selectedCategory)
        val updatesCollectionRef = lastPostDocRefCache.collection("Updates")

        val updatesSnapshot = updatesCollectionRef.get().await()

        nextUpdateNumberCache = if (updatesSnapshot.isEmpty) {
            1
        } else {
            updatesSnapshot.documents
                .maxOf { document ->
                    document.toObject(CheckPoint_Update_Item::class.java)?.update_Number ?: 0   //https://www.notion.so/Upload-Update-Checkpoint-23beef0772dc4bd2ab6442ce244d2580?pvs=4#9752541f872f40618b38fd93575895b3
                } + 1
        }
        nextUpdateNumberCache!!
    }

    //getLastPostDocumentRef solo te devuelve la referencia al post (no el post en si, solo como que el query) aca te devuelve el post posta
    suspend fun getLastPost(selectedCategory: String): Result<Post> = withContext(Dispatchers.IO){
        return@withContext try{
            val lastPostDocRef = getLastPostDocumentRef(selectedCategory)
            val postSnapshot = lastPostDocRef.get().await()
            val post = postSnapshot.toObject(Post::class.java)
            if (post != null) {
                Result.success(post)
            } else {
                Result.failure(Exception("Post not found"))
            }
        }catch (e:Exception){
            Result.failure(e)
        }
    }

    //https://www.notion.so/Upload-Update-Checkpoint-23beef0772dc4bd2ab6442ce244d2580?pvs=4#d218d93ee96040beb4413aa792072fc4
    companion object{
        private lateinit var lastPostDocRefCache:DocumentReference
        private var nextUpdateNumberCache: Int = 0
    }
}