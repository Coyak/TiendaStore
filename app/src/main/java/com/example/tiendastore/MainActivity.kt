package com.example.tiendastore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.tiendastore.ui.navigation.AppNavigation
import com.example.tiendastore.ui.theme.TiendaStoreTheme
import com.example.tiendastore.viewmodel.AuthViewModel
import com.example.tiendastore.viewmodel.ProductViewModel
import com.example.tiendastore.viewmodel.CartViewModel

class MainActivity : ComponentActivity() {
    private val authVM by viewModels<AuthViewModel>()
    private val productVM by viewModels<ProductViewModel>()
    private val cartVM by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Seed data
        authVM // ensure init
        productVM // ensure init

        setContent {
            TiendaStoreTheme {
                AppNavigation(authVM = authVM, productVM = productVM, cartVM = cartVM)
            }
        }
    }
}
