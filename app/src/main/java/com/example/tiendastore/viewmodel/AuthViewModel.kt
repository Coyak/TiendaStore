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

    private val _profile = MutableStateFlow<User?>(null)
    val profile: StateFlow<User?> = _profile.asStateFlow()

    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    init {
        // Observe current user (session)
        viewModelScope.launch {
            LocalStorage.currentUserFlow(appContext).collectLatest { user ->
                _currentUser.value = user
                refreshProfile()
            }
        }
        // Observe users to build full profile when session changes
        viewModelScope.launch {
            LocalStorage.usersFlow(appContext).collectLatest {
                refreshProfile()
            }
        }
        // Hydrate default admin if needed
        viewModelScope.launch { hydrate() }
    }

    fun clearMessages() {
        _ui.value = _ui.value.copy(errors = emptyMap(), message = null)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val errors = mutableMapOf<String, String>()
            val e = email.trim()
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()) errors["email"] = "Correo inválido"
            if (password.length < 6) errors["password"] = "Contraseña mínima 6 caracteres"
            if (errors.isNotEmpty()) {
                _ui.value = _ui.value.copy(errors = errors, message = null)
                return@launch
            }

            val users = LocalStorage.usersFlow(appContext).first()
            val found = users.firstOrNull { it.email.equals(e, ignoreCase = true) && it.password == password }
            if (found != null) {
                LocalStorage.setCurrentUser(appContext, found)
                _ui.value = _ui.value.copy(errors = emptyMap(), message = null)
            } else {
                _ui.value = _ui.value.copy(message = "Credenciales inválidas", errors = emptyMap())
            }
        }
    }

    fun register(name: String, email: String, password: String, isAdmin: Boolean) {
        viewModelScope.launch {
            val errors = mutableMapOf<String, String>()
            val n = name.trim()
            val e = email.trim()
            if (n.length < 3) errors["name"] = "Nombre mínimo 3 caracteres"
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()) errors["email"] = "Correo inválido"
            if (password.length < 6) errors["password"] = "Contraseña mínima 6 caracteres"

            val users = LocalStorage.usersFlow(appContext).first()
            if (users.any { it.email.equals(e, ignoreCase = true) }) {
                errors["email"] = "Correo ya registrado"
            }

            if (errors.isNotEmpty()) {
                _ui.value = _ui.value.copy(errors = errors, message = null)
                return@launch
            }

            // Por compatibilidad, usamos username = email
            val updated = users + User(username = e, password = password, isAdmin = isAdmin, name = n, email = e)
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
            val admin = User(username = "admin", password = "admin123", isAdmin = true, name = "Admin", email = "admin@local")
            LocalStorage.saveUsers(appContext, listOf(admin))
        }
    }

    fun updateProfile(name: String, email: String, address: String, city: String) {
        viewModelScope.launch {
            val current = _currentUser.value ?: return@launch
            val errors = mutableMapOf<String, String>()
            val n = name.trim()
            val e = email.trim()
            if (n.length < 3) errors["name"] = "Nombre mínimo 3 caracteres"
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()) errors["email"] = "Correo inválido"

            val users = LocalStorage.usersFlow(appContext).first()
            if (users.any { it.email.equals(e, ignoreCase = true) && it.username != current.username }) {
                errors["email"] = "Correo ya registrado"
            }
            if (errors.isNotEmpty()) {
                _ui.value = _ui.value.copy(errors = errors)
                return@launch
            }

            val updated = users.map { u ->
                if (u.username == current.username) u.copy(
                    name = n,
                    email = e,
                    address = address.trim(),
                    city = city.trim(),
                    username = e // mantener username=email para consistencia
                ) else u
            }
            LocalStorage.saveUsers(appContext, updated)
            // Actualizar sesión
            val newCurrent = updated.first { it.email.equals(e, true) }
            LocalStorage.setCurrentUser(appContext, newCurrent)
            _ui.value = _ui.value.copy(message = "Perfil actualizado")
        }
    }

    private suspend fun refreshProfile() {
        val session = _currentUser.value ?: run { _profile.value = null; return }
        val users = LocalStorage.usersFlow(appContext).first()
        val full = users.firstOrNull { it.username == session.username || it.email == session.username }
        _profile.value = full ?: session
    }
}

// no-op
