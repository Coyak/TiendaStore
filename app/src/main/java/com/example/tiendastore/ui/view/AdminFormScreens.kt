package com.example.tiendastore.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tiendastore.viewmodel.ProductFormState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddScreen(
    form: ProductFormState,
    onChange: (String, String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) { /* entrada limpia */ }
    AdminFormScaffold(title = "Agregar producto", form = form, onChange = onChange, onSave = onSave, onCancel = onCancel, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditScreen(
    id: Int?,
    form: ProductFormState,
    onStart: (Int?) -> Unit,
    onChange: (String, String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(id) { onStart(id) }
    AdminFormScaffold(title = if (form.id == null) "Editar" else "Editar #${form.id}", form = form, onChange = onChange, onSave = onSave, onCancel = onCancel, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminFormScaffold(
    title: String,
    form: ProductFormState,
    onChange: (String, String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(title) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = form.name,
                onValueChange = { onChange("name", it) },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(visible = form.errors["name"] != null) {
                Text(form.errors["name"] ?: "", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = form.price,
                onValueChange = { onChange("price", it) },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(visible = form.errors["price"] != null) {
                Text(form.errors["price"] ?: "", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = form.stock,
                onValueChange = { onChange("stock", it) },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(visible = form.errors["stock"] != null) {
                Text(form.errors["stock"] ?: "", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(8.dp))

            CategoryDropdown(selected = form.category, onSelected = { onChange("category", it) })
            AnimatedVisibility(visible = form.errors["category"] != null) {
                Text(form.errors["category"] ?: "", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = form.description,
                onValueChange = { onChange("description", it) },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            RowButtons(onSave = onSave, onCancel = onCancel, enabled = form.isValid)
        }
    }
}

@Composable
private fun RowButtons(onSave: () -> Unit, onCancel: () -> Unit, enabled: Boolean) {
    Row {
        Button(onClick = onSave, enabled = enabled) { Text("Guardar") }
        Spacer(Modifier.width(8.dp))
        Button(onClick = onCancel) { Text("Cancelar") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Consolas", "Juegos", "Accesorios", "Otros")

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(text = { Text(opt) }, onClick = {
                    onSelected(opt)
                    expanded = false
                })
            }
        }
    }
}
