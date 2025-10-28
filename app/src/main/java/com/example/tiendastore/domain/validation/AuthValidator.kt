package com.example.tiendastore.domain.validation

object AuthValidator {
    fun validateLogin(email: String, password: String): Map<String, String> {
        val e = email.trim()
        val errors = mutableMapOf<String, String>()
        if (e.isBlank() || e.length < 3) errors["email"] = "Correo inválido"
        if (password.length < 6) errors["password"] = "Contraseña mínima 6 caracteres"
        return errors
    }

    fun validateRegister(name: String, email: String, password: String, repeat: String): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        if (name.trim().length < 3) errors["name"] = "Nombre mínimo 3 caracteres"
        val e = email.trim()
        if (e.isBlank() || e.length < 3) errors["email"] = "Correo inválido"
        if (password.length < 6) errors["password"] = "Contraseña mínima 6 caracteres"
        if (password != repeat) errors["repeat"] = "Las contraseñas no coinciden"
        return errors
    }
}

