package com.example.tiendastore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendastore.data.DataBaseHelper
import com.example.tiendastore.data.toDomain
import com.example.tiendastore.data.toEntity
import com.example.tiendastore.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.tiendastore.domain.validation.AuthValidator

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
        // Observe session + users via Room
        viewModelScope.launch {
            DataBaseHelper.db(appContext).sessionDao().observe().collectLatest { sess ->
                val email = sess?.email
                if (email == null) {
                    _currentUser.value = null
                    _profile.value = null
                } else {
                    val ue = DataBaseHelper.db(appContext).userDao().getByEmail(email)
                    val u = ue?.toDomain()
                    _currentUser.value = u?.copy(password = "")
                    _profile.value = u
                }
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
            val errors = AuthValidator.validateLogin(email, password)
            if (errors.isNotEmpty()) {
                _ui.value = _ui.value.copy(errors = errors, message = null)
                return@launch
            }

            val found = DataBaseHelper.db(appContext).userDao().getByEmail(email.trim())
            if (found != null) {
                if (found.password != password) {
                    _ui.value = _ui.value.copy(message = "Credenciales inválidas", errors = emptyMap())
                    return@launch
                }
                DataBaseHelper.db(appContext).sessionDao().set(com.example.tiendastore.data.SessionEntity(email = found.email))
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

            val users = DataBaseHelper.db(appContext).userDao().observeAll().first()
            if (users.any { it.email.equals(e, ignoreCase = true) }) {
                errors["email"] = "Correo ya registrado"
            }

            if (errors.isNotEmpty()) {
                _ui.value = _ui.value.copy(errors = errors, message = null)
                return@launch
            }

            // Por compatibilidad, usamos username = email
            val entity = User(username = e, password = password, isAdmin = isAdmin, name = n, email = e).toEntity()
            DataBaseHelper.db(appContext).userDao().insert(entity)
            _ui.value = _ui.value.copy(errors = emptyMap(), message = "Cuenta creada, ahora ingresa")
        }
    }

    // legacy logout removed (now handled by Room below)

    suspend fun hydrate() {
        // Ensure admin user exists if users empty
        val users = DataBaseHelper.db(appContext).userDao().observeAll().first()
        if (users.isEmpty()) {
            val admin = User(username = "admin", password = "admin123", isAdmin = true, name = "Admin", email = "admin@local").toEntity()
            DataBaseHelper.db(appContext).userDao().insert(admin)
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

            val users = DataBaseHelper.db(appContext).userDao().observeAll().first()
            if (users.any { it.email.equals(e, ignoreCase = true) && it.email != current.email }) {
                errors["email"] = "Correo ya registrado"
            }
            if (errors.isNotEmpty()) {
                _ui.value = _ui.value.copy(errors = errors)
                return@launch
            }

            val entity = User(
                username = e,
                password = current.password,
                isAdmin = current.isAdmin,
                name = n,
                email = e,
                address = address.trim(),
                city = city.trim()
            ).toEntity()
            DataBaseHelper.db(appContext).userDao().update(entity)
            DataBaseHelper.db(appContext).sessionDao().set(com.example.tiendastore.data.SessionEntity(email = e))
            _ui.value = _ui.value.copy(message = "Perfil actualizado")
        }
    }

    fun logout() {
        viewModelScope.launch {
            DataBaseHelper.db(appContext).sessionDao().clear()
        }
    }
}

// no-op
