package com.example.tiendastore.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tiendastore.model.CartItem
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutScreen(
    items: List<CartItem>,
    total: Double,
    onChangeQty: (Long, Int) -> Unit,
    onRemove: (Long) -> Unit,
    onPay: () -> Unit,
    onBack: () -> Unit
) {
    var showSuccess by remember { mutableStateOf(false) }
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Carrito") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) } }
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items, key = { it.productId }) { it ->
                    Row(Modifier.fillMaxWidth()) {
                        com.example.tiendastore.ui.view.components.ImageFromPath(it.imagePath, Modifier.size(56.dp))
                        Column(Modifier.padding(start = 8.dp).weight(1f)) {
                            Text(it.name, style = MaterialTheme.typography.bodyMedium)
                            Text(formatPriceCLP(it.price))
                        }
                        QtyStepper(qty = it.qty, onMinus = { if (it.qty > 1) onChangeQty(it.productId, it.qty - 1) }, onPlus = { onChangeQty(it.productId, it.qty + 1) })
                        Spacer(Modifier.width(8.dp))
                        androidx.compose.material3.TextButton(onClick = { onRemove(it.productId) }) { Text("Quitar") }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total")
                Text(formatPriceCLP(total), style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { showSuccess = true }, enabled = items.isNotEmpty(), modifier = Modifier.fillMaxWidth()) { Text("Pagar") }
        }
    }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false },
            title = { Text("Compra exitosa") },
            text = { Text("Tu pago fue procesado correctamente.") },
            confirmButton = {
                TextButton(onClick = { showSuccess = false; onPay() }) { Text("OK") }
            }
        )
    }
}

@Composable
private fun QtyStepper(qty: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        androidx.compose.material3.OutlinedButton(onClick = onMinus, content = { Text("-") })
        Text(qty.toString(), modifier = Modifier.padding(horizontal = 8.dp))
        androidx.compose.material3.OutlinedButton(onClick = onPlus, content = { Text("+") })
    }
}

private fun formatPriceCLP(price: Double): String {
    val nf = NumberFormat.getNumberInstance(Locale("es", "CL"))
    nf.maximumFractionDigits = 0
    nf.minimumFractionDigits = 0
    return "$" + nf.format(price)
}
