package com.example.tiendastore.data.repository

import com.example.tiendastore.data.remote.ApiService
import com.example.tiendastore.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class UsuarioRepository(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): Result<Usuario> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(mapOf("email" to email, "password" to password))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error de login: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun registrar(usuario: Usuario): Result<Usuario> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.registrarUsuario(usuario)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error de registro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun actualizar(id: Long, usuario: Usuario): Result<Usuario> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.actualizarUsuario(id, usuario)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al actualizar: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    suspend fun checkHealth(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.checkHealth()
                response.isSuccessful
            } catch (e: Exception) {
                false
            }
        }
    }
}
