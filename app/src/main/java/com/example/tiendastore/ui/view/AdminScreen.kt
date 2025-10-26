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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tiendastore.model.Product
import com.example.tiendastore.viewmodel.ProductFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    form: ProductFormState,
    products: List<Product>,
    onChange: (String, String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Panel Admin") }, navigationIcon = {}) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Listado", style = MaterialTheme.typography.titleLarge)
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(products) { p ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text("#${p.id} - ${p.name}")
                            Text("${p.category} | ${p.stock} uds | ${p.price}")
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { onEdit(p.id) }) { Text("Editar") }
                            Button(onClick = { onDelete(p.id) }) { Text("Eliminar") }
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }

            Text(if (form.id == null) "Nuevo producto" else "Editar #${form.id}", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = form.name,
                onValueChange = { onChange("name", it) },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(visible = form.errors["name"] != null) {
                Text(form.errors["name"] ?: "", color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = form.price,
                onValueChange = { onChange("price", it) },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(visible = form.errors["price"] != null) {
                Text(form.errors["price"] ?: "", color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = form.stock,
                onValueChange = { onChange("stock", it) },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(visible = form.errors["stock"] != null) {
                Text(form.errors["stock"] ?: "", color = MaterialTheme.colorScheme.error)
            }

            CategoryDropdown(selected = form.category, onSelected = { onChange("category", it) })
            AnimatedVisibility(visible = form.errors["category"] != null) {
                Text(form.errors["category"] ?: "", color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = form.description,
                onValueChange = { onChange("description", it) },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onSave, enabled = form.isValid) { Text("Guardar") }
                Button(onClick = onCancel) { Text("Cancelar") }
                Spacer(Modifier.weight(1f))
                Button(onClick = onBack) { Text("Volver") }
            }
        }
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
            modifier = Modifier.fillMaxWidth()
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
