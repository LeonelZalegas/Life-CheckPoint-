package com.example.influencer.Core.Data.Repositories.UserRepository

import com.example.influencer.Core.Data.Network.AuthenticationService
import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


//este repositorio es unicamente para guardar y retrieve datos relacionados a los Usuarios (sus emails, fotos de perfil, etc)
@Singleton
class FirestoreUserRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val authService: AuthenticationService
): UserRepository {

    override suspend fun getUserProfilePictureUrl(): String = withContext(Dispatchers.IO) {
        val uid = authService.getUid()

        val userDocSnapshot = db.collection("Usuarios").document(uid).get().await()
        return@withContext userDocSnapshot.getString("profilePictureUrl") ?: ""
    }

    override suspend fun saveUserAgeMonths(age: Int, months: Int): Unit = withContext(Dispatchers.IO){
        val uid = authService.getUid()

        val userDocRef = db.collection("Usuarios").document(uid)
        userDocRef.update("years_old", age, "months_old", months).await()
    }

    override suspend fun saveUserCountry(countryName: String, countryFlag: String) {
        val uid = authService.getUid()

        val userDocRef = db.collection("Usuarios").document(uid)
        userDocRef.update("countryName", countryName, "countryFlagCode", countryFlag).await()
    }

    override suspend fun getUserById(userId: String?): Result<UsuarioSignin> = withContext(Dispatchers.IO) {
        try {
            val uid = userId ?: authService.getUid()
            val userDocSnapshot = db.collection("Usuarios").document(uid).get().await()
            val user = userDocSnapshot.toObject(UsuarioSignin::class.java)
            user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Failed to parse user document"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUserId():String = withContext(Dispatchers.IO) {
        authService.getUid()
    }

    override suspend fun isFollowing(currentUserId: String, targetUserId: String): Boolean = withContext(Dispatchers.IO) {
        val chunks = db.collection("Usuarios").document(currentUserId).collection("following").get().await()
        for (document in chunks.documents) {
            val userIds = document.get("userIds") as List<String>
            if (userIds.contains(targetUserId)) {
                return@withContext true
            }
        }
        return@withContext false
    }

    override suspend fun followUser(targetUserId: String) = withContext(Dispatchers.IO) {
        val currentUserId = authService.getUid()
        addUserToChunk(currentUserId, targetUserId, "following")
        addUserToChunk(targetUserId, currentUserId, "followers")
        incrementFollowCount(currentUserId, targetUserId, increment = true)
    }

    override suspend fun unfollowUser(targetUserId: String) = withContext(Dispatchers.IO) {
        val currentUserId = authService.getUid()
        removeUserFromChunk(currentUserId, targetUserId, "following")
        removeUserFromChunk(targetUserId, currentUserId, "followers")
        incrementFollowCount(currentUserId, targetUserId, increment = false)
    }

    private suspend fun addUserToChunk(userId: String, targetUserId: String, collection: String) {
        val userRef = db.collection("Usuarios").document(userId)
        val chunkSize = 500

        val chunks = userRef.collection(collection).get().await().documents
        var lastChunkRef = userRef.collection(collection).document("chunk_${chunks.size}")

        if (chunks.isNotEmpty()) {
            val lastChunkUserIds = chunks.last().get("userIds") as? List<String>
            if (lastChunkUserIds != null && lastChunkUserIds.size >= chunkSize) {
                lastChunkRef = userRef.collection(collection).document("chunk_${chunks.size + 1}")
            }
        }else{
            lastChunkRef = userRef.collection(collection).document("chunk_1")
            db.runTransaction { transaction ->
                transaction.set(lastChunkRef, mapOf("userIds" to listOf<String>()), SetOptions.merge())
                transaction.update(lastChunkRef, "userIds", FieldValue.arrayUnion(targetUserId))
            }.await()
        }

        db.runTransaction { transaction ->
            transaction.update(lastChunkRef, "userIds", FieldValue.arrayUnion(targetUserId))
        }.await()
    }

    private suspend fun removeUserFromChunk(userId: String, targetUserId: String, collection: String) {
        val userRef = db.collection("Usuarios").document(userId)

        val chunks = userRef.collection(collection).get().await().documents
        db.runTransaction { transaction ->
            for (document in chunks) {
                val userIds = document.get("userIds") as List<String>
                if (userIds.contains(targetUserId)) {
                    transaction.update(document.reference, "userIds", FieldValue.arrayRemove(targetUserId))
                    break
                }
            }
        }.await()
    }

    private suspend fun incrementFollowCount(currentUserId: String, targetUserId: String, increment: Boolean) {
        val currentUserRef = db.collection("Usuarios").document(currentUserId)
        val targetUserRef = db.collection("Usuarios").document(targetUserId)

        db.runTransaction { transaction ->
            val currentUserSnapshot = transaction.get(currentUserRef)
            val targetUserSnapshot = transaction.get(targetUserRef)

            val currentUserFollowingCount = currentUserSnapshot.getLong("followingCount") ?: 0
            val targetUserFollowersCount = targetUserSnapshot.getLong("followersCount") ?: 0

            val newCurrentUserFollowingCount = if (increment) currentUserFollowingCount + 1 else currentUserFollowingCount - 1
            val newTargetUserFollowersCount = if (increment) targetUserFollowersCount + 1 else targetUserFollowersCount - 1

            transaction.update(currentUserRef, "followingCount", newCurrentUserFollowingCount)
            transaction.update(targetUserRef, "followersCount", newTargetUserFollowersCount)
        }.await()
    }
}