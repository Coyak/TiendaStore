package com.example.tiendastore.model

data class User(
    val username: String,
    val password: String,
    val isAdmin: Boolean = false,
    val name: String = "",
    val email: String = "",
    val address: String = "",
    val city: String = ""
)
