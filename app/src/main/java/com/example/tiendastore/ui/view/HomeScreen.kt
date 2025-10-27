package com.example.tiendastore.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tiendastore.model.Product
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    products: List<Product>,
    isAdmin: Boolean,
    onLogout: () -> Unit,
    onAdmin: () -> Unit,
    onProductClick: (Int) -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("TiendaStore") }, actions = {
            Row(modifier = Modifier.padding(end = 8.dp)) {
                if (isAdmin) {
                    Button(onClick = onAdmin) { Text("Panel admin") }
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onLogout) { Text("Cerrar sesión") }
            }
        })
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Productos", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(products) { p ->
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProductClick(p.id) }
                    ) {
                        Text(p.name, style = MaterialTheme.typography.titleMedium)
                        Text("Precio: ${formatPriceCLP(p.price)}")
                        Text("Stock: ${p.stock}")
                        Text("Categoría: ${p.category}")
                        if (p.description.isNotBlank()) {
                            Text(p.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

private fun formatPriceCLP(price: Double): String {
    val nf = NumberFormat.getNumberInstance(Locale("es", "CL"))
    nf.maximumFractionDigits = 0
    nf.minimumFractionDigits = 0
    return "$" + nf.format(price)
}
