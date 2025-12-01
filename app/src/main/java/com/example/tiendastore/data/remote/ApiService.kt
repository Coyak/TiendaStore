package com.example.tiendastore.data.remote

import com.example.tiendastore.model.Producto
import com.example.tiendastore.model.Usuario
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- Health ---
    @GET("api/health")
    suspend fun checkHealth(): Response<Map<String, String>>

    // --- Usuarios ---
    @GET("api/usuarios")
    suspend fun listarUsuarios(): List<Usuario>

    @POST("api/usuarios/registro")
    suspend fun registrarUsuario(@Body usuario: Usuario): Response<Usuario>

    @POST("api/usuarios/login")
    suspend fun login(@Body loginRequest: Map<String, String>): Response<Usuario>

    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(@Path("id") id: Long, @Body usuario: Usuario): Response<Usuario>

    // --- Productos ---
    @GET("api/productos")
    suspend fun listarProductos(): List<Producto>

    @GET("api/productos/{id}")
    suspend fun obtenerProducto(@Path("id") id: Long): Response<Producto>

    @POST("api/productos")
    suspend fun crearProducto(@Body producto: Producto): Response<Producto>

    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(@Path("id") id: Long, @Body producto: Producto): Response<Producto>

    @DELETE("api/productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Long): Response<Void>
}
