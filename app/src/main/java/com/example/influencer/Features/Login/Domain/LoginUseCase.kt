package com.example.influencer.Features.Login.Domain

import javax.inject.Inject
import com.example.influencer.Core.Data.Network.AuthenticationService
import com.example.influencer.Features.Login.Domain.Model.UsuarioLogin
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

//aca lo 100% ideal seria o ya sea usar RxJava (un Single (equivalante al floawble pero para 1 unica operacion asyncronica nada mas), Completable, maybe) o corrutinas de Kotlin
//ya que al usar callBacks si bien el UseCase no maneja la implementacion de "task.isSuccessful()" si es conciente de la existencia de la interfaz (Listener), que seria un componente de
//que lo implementa el viewmodel, aun asi, dentro de todo sigue cumpliendo un MVVM con clean Architecure ya que depsues de todo no sabe ninguna implementacion (todo lo hace el viewmodel)
class LoginUseCase @Inject constructor(
    private val authenticationService: AuthenticationService
) {
    suspend operator fun invoke(usuarioLogin: UsuarioLogin) {
        return suspendCancellableCoroutine { continuation ->
            authenticationService.login(usuarioLogin.email, usuarioLogin.contrasena)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(task.exception ?: Exception("Login failed"))
                    }
                }
        }
    }
}