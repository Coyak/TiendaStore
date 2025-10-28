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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tiendastore.viewmodel.AuthUiState

@Composable
fun RegisterScreen(
    uiState: AuthUiState,
    onRegister: (String, String, String, Boolean) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    val tfColors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    colors = tfColors,
                    modifier = Modifier.fillMaxWidth()
                )
                AnimatedVisibility(visible = uiState.errors["name"] != null) {
                    Text(uiState.errors["name"] ?: "", color = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") },
                    singleLine = true,
                    colors = tfColors,
                    modifier = Modifier.fillMaxWidth()
                )
                AnimatedVisibility(visible = uiState.errors["email"] != null) {
                    Text(uiState.errors["email"] ?: "", color = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = tfColors,
                    modifier = Modifier.fillMaxWidth()
                )
                AnimatedVisibility(visible = uiState.errors["password"] != null) {
                    Text(uiState.errors["password"] ?: "", color = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = repeat,
                    onValueChange = { repeat = it },
                    label = { Text("Repetir contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = tfColors,
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
                        if (localError == null) onRegister(name, email, password, isAdmin)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Registrar") }

                Spacer(Modifier.height(8.dp))
                androidx.compose.material3.OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Volver") }
            }
        }

        AnimatedVisibility(visible = uiState.message != null) {
            Spacer(Modifier.height(12.dp))
            Text(uiState.message ?: "")
        }
    }
}
