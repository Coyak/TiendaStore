package com.example.tiendastore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendastore.domain.validation.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val errors: Map<String, String> = emptyMap(),
    val message: String? = null
)

class AuthViewModel(
    application: Application,
    private val repository: com.example.tiendastore.data.repository.UsuarioRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        com.example.tiendastore.data.repository.UsuarioRepository(com.example.tiendastore.data.remote.RetrofitClient.apiService)
    )

    private val _currentUser = MutableStateFlow<com.example.tiendastore.model.Usuario?>(null)
    val currentUser: StateFlow<com.example.tiendastore.model.Usuario?> = _currentUser.asStateFlow()

    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()



    private val _connectionStatus = MutableStateFlow<Boolean?>(null)
    val connectionStatus: StateFlow<Boolean?> = _connectionStatus.asStateFlow()

    init {
        checkBackendConnection()
    }

    private fun checkBackendConnection() {
        viewModelScope.launch {
            _connectionStatus.value = repository.checkHealth()
        }
    }

    fun clearMessages() {
        _ui.value = _ui.value.copy(errors = emptyMap(), message = null)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val errors = AuthValidator.validateLogin(email, password)
            if (errors.isNotEmpty()) {
                _ui.value = _ui.value.copy(errors = errors, message = null)
                return@launch
            }

            val result = repository.login(email, password)
            if (result.isSuccess) {
                val user = result.getOrNull()
                _currentUser.value = user
                _ui.value = _ui.value.copy(errors = emptyMap(), message = null)
            } else {
                _ui.value = _ui.value.copy(message = "Credenciales inválidas", errors = emptyMap())
            }
        }
    }

    fun register(name: String, email: String, password: String, isAdmin: Boolean) {
        viewModelScope.launch {
            val n = name.trim()
            val e = email.trim()
            val errors = AuthValidator.validateRegister(n, e, password, password).toMutableMap()

            if (errors.isNotEmpty()) {
                _ui.value = _ui.value.copy(errors = errors, message = null)
                return@launch
            }

            val newUser = com.example.tiendastore.model.Usuario(
                nombre = n,
                email = e,
                password = password,
                rol = if (isAdmin) "ADMIN" else "USER"
            )

            val result = repository.registrar(newUser)
            if (result.isSuccess) {
                _ui.value = _ui.value.copy(errors = emptyMap(), message = "Cuenta creada, ahora ingresa")
            } else {
                _ui.value = _ui.value.copy(errors = mapOf("email" to "Error al registrar"), message = null)
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun updateProfile(name: String, email: String, address: String, city: String) {
        val current = _currentUser.value ?: return
        viewModelScope.launch {
            val updatedUser = current.copy(
                nombre = name,
                email = email,
                direccion = address,
                ciudad = city
            )
            val result = repository.actualizar(current.id, updatedUser)
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                _ui.value = _ui.value.copy(message = "Perfil actualizado")
            } else {
                _ui.value = _ui.value.copy(errors = mapOf("email" to "Error al actualizar"))
            }
        }
    }
}

// no-op
