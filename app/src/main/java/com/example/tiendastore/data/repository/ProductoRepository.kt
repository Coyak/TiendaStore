package com.example.tiendastore.data.repository

import com.example.tiendastore.data.remote.ApiService
import com.example.tiendastore.model.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductoRepository(private val apiService: ApiService) {

    suspend fun listarProductos(): Result<List<Producto>> {
        return withContext(Dispatchers.IO) {
            try {
                val productos = apiService.listarProductos()
                Result.success(productos)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun crearProducto(producto: Producto): Result<Producto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.crearProducto(producto)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al crear producto"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun eliminarProducto(id: Long): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.eliminarProducto(id)
                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    Result.failure(Exception("Error al eliminar"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
