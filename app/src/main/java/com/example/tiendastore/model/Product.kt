package com.example.tiendastore.model

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val stock: Int,
    val category: String,
    val description: String = "",
    val imagePath: String? = null
)
