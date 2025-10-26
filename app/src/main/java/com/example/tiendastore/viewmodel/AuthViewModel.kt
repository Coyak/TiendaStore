package com.example.tiendastore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendastore.data.LocalStorage
import com.example.tiendastore.data.dataStore
import com.example.tiendastore.model.User
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

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = getApplication<Application>()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    init {
        // Observe current user
        viewModelScope.launch {
            LocalStorage.currentUserFlow(appContext).collectLatest { user ->
                _currentUser.value = user
            }
        }
        // Hydrate default admin if needed
        viewModelScope.launch { hydrate() }
    }

    fun clearMessages() {
        _ui.value = _ui.value.copy(errors = emptyMap(), message = null)
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val errors = mutableMapOf<String, String>()
            if (username.trim().length < 3) errors["username"] = "Usuario mínimo 3 caracteres"
            if (password.length < 6) errors["password"] = "Contraseña mínima 6 caracteres"
            if (errors.isNotEmpty()) {
                _ui.value = _ui.value.copy(errors = errors, message = null)
                return@launch
            }

            val users = LocalStorage.usersFlow(appContext).first()
            val found = users.firstOrNull { it.username == username && it.password == password }
            if (found != null) {
                LocalStorage.setCurrentUser(appContext, found)
                _ui.value = _ui.value.copy(errors = emptyMap(), message = null)
            } else {
                _ui.value = _ui.value.copy(message = "Credenciales inválidas", errors = emptyMap())
            }
        }
    }

    fun register(username: String, password: String, isAdmin: Boolean) {
        viewModelScope.launch {
            val errors = mutableMapOf<String, String>()
            val u = username.trim()
            if (u.length < 3) errors["username"] = "Usuario mínimo 3 caracteres"
            if (password.length < 6) errors["password"] = "Contraseña mínima 6 caracteres"

            val users = LocalStorage.usersFlow(appContext).first()
            if (users.any { it.username == u }) {
                errors["username"] = "Usuario ya existe"
            }

            if (errors.isNotEmpty()) {
                _ui.value = _ui.value.copy(errors = errors, message = null)
                return@launch
            }

            val updated = users + User(u, password, isAdmin)
            LocalStorage.saveUsers(appContext, updated)
            _ui.value = _ui.value.copy(errors = emptyMap(), message = "Cuenta creada, ahora ingresa")
        }
    }

    fun logout() {
        viewModelScope.launch {
            LocalStorage.setCurrentUser(appContext, null)
        }
    }

    suspend fun hydrate() {
        // Ensure admin user exists if users empty
        val users = LocalStorage.usersFlow(appContext).first()
        if (users.isEmpty()) {
            val admin = User(username = "admin", password = "admin123", isAdmin = true)
            LocalStorage.saveUsers(appContext, listOf(admin))
        }
    }
}

// no-op
