package com.example.tiendastore.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tiendastore.viewmodel.AuthUiState

@Composable
fun RegisterScreen(
    uiState: AuthUiState,
    onRegister: (String, String, Boolean) -> Unit,
    onBack: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        AnimatedVisibility(visible = uiState.errors["username"] != null) {
            Text(uiState.errors["username"] ?: "", color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        AnimatedVisibility(visible = uiState.errors["password"] != null) {
            Text(uiState.errors["password"] ?: "", color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = repeat,
            onValueChange = { repeat = it },
            label = { Text("Repetir contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        AnimatedVisibility(visible = localError != null) {
            Text(localError ?: "", color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it })
            Text("Administrador (solo pruebas)")
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                localError = if (password != repeat) "Las contraseñas no coinciden" else null
                if (localError == null) onRegister(username, password, isAdmin)
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Registrar") }

        Spacer(Modifier.height(8.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Volver") }

        AnimatedVisibility(visible = uiState.message != null) {
            Spacer(Modifier.height(12.dp))
            Text(uiState.message ?: "")
        }
    }
}

