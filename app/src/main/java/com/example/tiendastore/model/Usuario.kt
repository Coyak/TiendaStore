package com.example.tiendastore.model

data class Usuario(
    val id: Long = 0,
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val rol: String = "USER", // USER o ADMIN
    val direccion: String = "",
    val ciudad: String = ""
)
