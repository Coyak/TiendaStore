package com.example.tiendastore.model

data class User(
    val username: String,
    val password: String,
    val isAdmin: Boolean = false
)

