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
import androidx.compose.material3.Button
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import com.example.tiendastore.model.CartItem
import androidx.compose.material.icons.filled.ShoppingCart
import com.example.tiendastore.ui.view.components.CartQuickSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tiendastore.model.Producto
import java.text.NumberFormat
import java.util.Locale
import com.example.tiendastore.ui.view.components.ImageFromPath

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Producto?,
    onBack: () -> Unit,
    onAddToCart: (Producto?) -> Unit,
    cartCount: Int,
    cartItems: List<CartItem>,
    cartTotal: Double,
    onCartChangeQty: (Long, Int) -> Unit,
    onCartRemove: (Long) -> Unit,
    onGoCheckout: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showCart by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("TiendaStore") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Volver") } },
            actions = {
                IconButton(onClick = { showCart = true }) {
                    BadgedBox(badge = { if (cartCount > 0) Badge { Text(cartCount.toString()) } }) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                    }
                }
            }
        )
    }, snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
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
            ImageFromPath(product.imagenUrl, Modifier.fillMaxWidth().aspectRatio(1f), name = product.nombre)
            Spacer(Modifier.padding(8.dp))
            Text(product.nombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.padding(4.dp))
            Text(formatPriceCLP(product.precio), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.padding(8.dp))
            Button(onClick = {
                onAddToCart(product)
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Producto agregado",
                        actionLabel = "Ver carrito"
                    )
                    if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                        showCart = true
                    }
                }
            }) { Text("Agregar al carrito") }
            Spacer(Modifier.padding(8.dp))

            // Info principal
            Text("Stock: ${product.stock}", style = MaterialTheme.typography.bodyMedium)
            if (product.descripcion.isNotBlank()) {
                Spacer(Modifier.padding(8.dp))
                Text(product.descripcion, style = MaterialTheme.typography.bodyLarge)
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

    if (showCart) {
        CartQuickSheet(
            items = cartItems,
            total = cartTotal,
            onChangeQty = onCartChangeQty,
            onRemove = onCartRemove,
            onGoCheckout = onGoCheckout,
            onDismiss = { showCart = false }
        )
    }
}

private fun formatPriceCLP(price: Double): String {
    val nf = NumberFormat.getNumberInstance(Locale("es", "CL"))
    nf.maximumFractionDigits = 0
    nf.minimumFractionDigits = 0
    return "$" + nf.format(price)
}
