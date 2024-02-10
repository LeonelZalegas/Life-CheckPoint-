package com.example.influencer.Data.Repositories.FirestorePostRepository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//modulo para instanciar la interfaz PostRepository, aca lo que hacemos es decir que la implementacion (de los metodos de la interfaz) que hilt va a utilizar en sus inyecciones
//va a ser FirestorePostRepository, es decir cada vez que inyectemos PostRepository (en este caso creo que SavePostUseCase) pos va a usar FirestorePostRepository
@Module
@InstallIn(SingletonComponent::class) // SingletonComponent is used here assuming you want PostRepository to be available application-wide.
abstract class PostRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPostRepository(
        firestorePostRepository: FirestorePostRepository
    ): PostRepository
}
