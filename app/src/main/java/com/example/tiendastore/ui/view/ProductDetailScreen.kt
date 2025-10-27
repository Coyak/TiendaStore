package com.example.tiendastore.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tiendastore.model.Product
import java.text.NumberFormat
import java.util.Locale
import com.example.tiendastore.ui.view.components.ImageFromPath

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(product: Product?, onBack: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(product?.name ?: "Detalle") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Volver") } }
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (product == null) {
                Text("Producto no encontrado", color = MaterialTheme.colorScheme.error)
                return@Column
            }

            // Título y precio destacado
            ImageFromPath(product.imagePath, Modifier.fillMaxWidth().aspectRatio(1f))
            Spacer(Modifier.padding(8.dp))
            Text(product.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.padding(4.dp))
            Text(formatPriceCLP(product.price), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.padding(8.dp))

            // Info principal
            Text("Categoría: ${product.category}", style = MaterialTheme.typography.bodyMedium)
            Text("Stock: ${product.stock}", style = MaterialTheme.typography.bodyMedium)
            if (product.description.isNotBlank()) {
                Spacer(Modifier.padding(8.dp))
                Text(product.description, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(Modifier.weight(1f))

            // ID sutil al final
            val subtle = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            Text(
                text = "ID: ${product.id}",
                color = subtle,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun formatPriceCLP(price: Double): String {
    val nf = NumberFormat.getNumberInstance(Locale("es", "CL"))
    nf.maximumFractionDigits = 0
    nf.minimumFractionDigits = 0
    return "$" + nf.format(price)
}
