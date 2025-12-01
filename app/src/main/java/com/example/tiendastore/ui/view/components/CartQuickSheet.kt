package com.example.tiendastore.ui.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tiendastore.model.CartItem
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartQuickSheet(
    items: List<CartItem>,
    total: Double,
    onChangeQty: (Long, Int) -> Unit,
    onRemove: (Long) -> Unit,
    onGoCheckout: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("Tu carrito", modifier = Modifier.padding(bottom = 8.dp))
            if (items.isEmpty()) {
                Text("Aún no hay productos.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items, key = { it.productId }) { it ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ImageFromPath(it.imagePath, Modifier.size(48.dp), name = it.name)
                            Column(Modifier.padding(start = 8.dp).weight(1f)) {
                                Text(it.name)
                                Text(formatPriceCLP(it.price))
                            }
                            QtyMini(
                                qty = it.qty,
                                onMinus = { if (it.qty > 1) onChangeQty(it.productId, it.qty - 1) },
                                onPlus = { onChangeQty(it.productId, it.qty + 1) }
                            )
                            Spacer(Modifier.width(6.dp))
                            TextButton(onClick = { onRemove(it.productId) }) { Text("Quitar") }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total")
                    Text(formatPriceCLP(total))
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { onDismiss(); onGoCheckout() }, modifier = Modifier.fillMaxWidth()) { Text("Ir a pagar") }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun QtyMini(qty: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onMinus) { androidx.compose.material3.Icon(Icons.Filled.Remove, contentDescription = "-" ) }
        Text(
            qty.toString(),
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center
        )
        IconButton(onClick = onPlus) { androidx.compose.material3.Icon(Icons.Filled.Add, contentDescription = "+" ) }
    }
}

private fun formatPriceCLP(price: Double): String {
    val nf = NumberFormat.getNumberInstance(Locale("es", "CL"))
    nf.maximumFractionDigits = 0
    nf.minimumFractionDigits = 0
    return "$" + nf.format(price)
}
