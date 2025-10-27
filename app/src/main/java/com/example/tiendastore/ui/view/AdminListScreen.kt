package com.example.tiendastore.ui.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tiendastore.model.Product
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminListScreen(
    products: List<Product>,
    onAdd: () -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onBack: () -> Unit
) {
    var pendingDeleteId by remember { mutableStateOf<Int?>(null) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos (Admin)") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(products) { p ->
                ProductListItem(
                    product = p,
                    onEdit = { onEdit(p.id) },
                    onDelete = { pendingDeleteId = p.id }
                )
                Divider()
            }
        }
    }

    val id = pendingDeleteId
    if (id != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("Eliminar producto") },
            text = { Text("¿Seguro que deseas eliminar el producto #$id?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(id)
                    pendingDeleteId = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun ProductListItem(product: Product, onEdit: () -> Unit, onDelete: () -> Unit) {
    androidx.compose.material3.ListItem(
        headlineContent = { Text(product.name, style = MaterialTheme.typography.titleMedium) },
        supportingContent = {
            Text("${product.category} • ${product.stock} uds • ${formatPriceCLP(product.price)}")
        },
        trailingContent = {
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Outlined.Edit, contentDescription = "Editar") }
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Outlined.DeleteForever,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    )
}

private fun formatPriceCLP(price: Double): String {
    val nf = java.text.NumberFormat.getNumberInstance(java.util.Locale("es", "CL"))
    nf.maximumFractionDigits = 0
    nf.minimumFractionDigits = 0
    return "$" + nf.format(price)
}
