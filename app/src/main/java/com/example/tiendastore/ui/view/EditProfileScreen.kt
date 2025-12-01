package com.example.tiendastore.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tiendastore.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    user: Usuario?,
    errors: Map<String, String>,
    onSave: (String, String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var name by remember(user) { mutableStateOf(user?.nombre ?: "") }
    var email by remember(user) { mutableStateOf(user?.email ?: "") }
    var address by remember(user) { mutableStateOf(user?.direccion ?: "") }
    var city by remember(user) { mutableStateOf(user?.ciudad ?: "") }
    val original = remember(user) { user?.copy() }
    var askConfirm by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Editar perfil") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) } }
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            if (errors["name"] != null) Text(errors["name"]!!, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            if (errors["email"] != null) Text(errors["email"]!!, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Dirección (opcional)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Ciudad (opcional)") }, singleLine = true, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                if (original != null) {
                    askConfirm = true
                } else {
                    onSave(name, email, address, city)
                }
            }, enabled = user != null) { Text("Guardar cambios") }
        }
    }

    if (askConfirm && original != null) {
        val changes = buildList {
            if (original.nombre != name) add("Nombre")
            if (original.email != email) add("Correo")
            if (original.direccion != address) add("Dirección")
            if (original.ciudad != city) add("Ciudad")
        }
        val msg = if (changes.isEmpty()) "No hay cambios para guardar" else "Se modificarán: ${changes.joinToString(", ")}"
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { askConfirm = false },
            title = { Text("Confirmar cambios") },
            text = { Text(msg) },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    askConfirm = false
                    if (changes.isNotEmpty()) onSave(name, email, address, city)
                }) { Text("Guardar") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { askConfirm = false }) { Text("Cancelar") }
            }
        )
    }
}
