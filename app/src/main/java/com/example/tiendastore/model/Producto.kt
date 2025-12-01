package com.example.tiendastore.model

import java.io.Serializable

data class Producto(
    val id: Long = 0,
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val stock: Int = 0,
    val imagenUrl: String = ""
) : Serializable
