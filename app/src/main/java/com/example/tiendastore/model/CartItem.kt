package com.example.tiendastore.model

data class CartItem(
    val productId: Int,
    val name: String,
    val price: Double,
    val qty: Int,
    val imagePath: String? = null
)

