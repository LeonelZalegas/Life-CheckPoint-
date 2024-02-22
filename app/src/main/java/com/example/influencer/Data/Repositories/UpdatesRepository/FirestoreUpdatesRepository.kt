package com.example.influencer.Data.Repositories.UpdatesRepository

import com.example.influencer.Data.Network.AuthenticationService
import com.example.influencer.UI.Upload_New_Update_Checkpoint.Model.CheckPoint_Update_Item
import com.google.firebase.firestore.DocumentReference
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

    private suspend fun getLastPostDocumentRef(selectedCategory: String):DocumentReference = withContext(Dispatchers.IO){
        val uid = authService.getUid()
        val userDocRef = db.collection("Usuarios").document(uid)
        val postsCollectionRef = userDocRef.collection("Posts")

        return@withContext postsCollectionRef
            .whereEqualTo("selectedCategory", selectedCategory)
            .orderBy("creationDate", Query.Direction.DESCENDING)
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
    }

    override suspend fun getNextUpdateNumber(selectedCategory: String): Int = withContext(Dispatchers.IO) {
        lastPostDocRefCache = getLastPostDocumentRef(selectedCategory)
        val updatesCollectionRef = lastPostDocRefCache.collection("Updates")

        val updatesSnapshot = updatesCollectionRef.get().await()

        nextUpdateNumberCache = if (updatesSnapshot.isEmpty) {
            1
        } else {
            updatesSnapshot.documents
                .maxOf { document ->
                    document.toObject(CheckPoint_Update_Item::class.java)?.update_Number ?: 0
                } + 1
        }

        nextUpdateNumberCache!!
    }

    companion object{
        private lateinit var lastPostDocRefCache:DocumentReference
        private var nextUpdateNumberCache: Int? = null
    }

}