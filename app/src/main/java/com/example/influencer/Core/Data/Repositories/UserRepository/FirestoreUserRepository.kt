package com.example.influencer.Core.Data.Repositories.UserRepository

import com.example.influencer.Core.Data.Network.AuthenticationService
import com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


//este repositorio es unicamente para guardar y retrieve datos relacionados a los Usuarios (sus emails, fotos de perfil, etc)
//TODO pasar el CheckpointChooserRowRepo aca
@Singleton
class FirestoreUserRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val authService: _root_ide_package_.com.example.influencer.Core.Data.Network.AuthenticationService
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

    // Fetch a random user with at least one post
    override suspend fun getRandomUserDocument(): Result<_root_ide_package_.com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin> = withContext(Dispatchers.IO){
        try {

        val usersSnapshot = db.collection("Usuarios")
            .whereGreaterThan("postCount",0)
            .get()
            .await()

        if (usersSnapshot.documents.isNotEmpty()) {
            val randomUserDoc = usersSnapshot.documents.random()
            val user = randomUserDoc.toObject(_root_ide_package_.com.example.influencer.Features.SignIn.Domain.Model.UsuarioSignin::class.java)
            user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Failed to parse user document"))
        } else {
            Result.failure(Exception("No users with posts found"))
        }

      }catch (e: Exception){
            Result.failure(e)
      }
    }


}