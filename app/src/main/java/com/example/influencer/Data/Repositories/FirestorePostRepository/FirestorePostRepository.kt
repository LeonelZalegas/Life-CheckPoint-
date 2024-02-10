package com.example.influencer.Data.Repositories.FirestorePostRepository

import com.example.influencer.Data.Network.AuthenticationService
import com.example.influencer.UI.Upload_New_Checkpoint.Model.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// Concrete implementation of PostRepository using Firebase Firestore
@Singleton
class FirestorePostRepository @Inject constructor(
    private val db:FirebaseFirestore,
    private val authService: AuthenticationService
): PostRepository {

    override suspend fun savePost(post: Post): Task<Void> = withContext(Dispatchers.IO){
        val uid = authService.getUid()

        val userDocRef = db.collection("Usuarios").document(uid)
        val postsCollectionRef = userDocRef.collection("Posts")
        return@withContext postsCollectionRef.document().set(post)
    }
}