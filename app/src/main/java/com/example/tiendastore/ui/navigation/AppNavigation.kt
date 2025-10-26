package com.example.tiendastore.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import com.example.tiendastore.viewmodel.AuthViewModel
import com.example.tiendastore.viewmodel.ProductViewModel
import com.example.tiendastore.ui.view.AdminScreen
import com.example.tiendastore.ui.view.HomeScreen
import com.example.tiendastore.ui.view.LoginScreen
import com.example.tiendastore.ui.view.RegisterScreen

enum class Screen { LOGIN, REGISTER, HOME, ADMIN }

@Composable
fun AppNavigation(authVM: AuthViewModel, productVM: ProductViewModel) {
    var screen by remember { mutableStateOf(Screen.LOGIN) }
    val currentUser by authVM.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        screen = if (currentUser == null) Screen.LOGIN else Screen.HOME
    }

    when (screen) {
        Screen.LOGIN -> LoginScreen(
            uiState = authVM.ui.collectAsState().value,
            onLogin = { u, p -> authVM.login(u, p) },
            onGoRegister = { screen = Screen.REGISTER }
        )
        Screen.REGISTER -> RegisterScreen(
            uiState = authVM.ui.collectAsState().value,
            onRegister = { u, p, isAdmin -> authVM.register(u, p, isAdmin) },
            onBack = { screen = Screen.LOGIN }
        )
        Screen.HOME -> HomeScreen(
            products = productVM.products.collectAsState().value,
            isAdmin = currentUser?.isAdmin == true,
            onLogout = { authVM.logout() },
            onAdmin = { screen = Screen.ADMIN }
        )
        Screen.ADMIN -> AdminScreen(
            form = productVM.form.collectAsState().value,
            products = productVM.products.collectAsState().value,
            onChange = { field, value -> productVM.onFieldChange(field, value) },
            onSave = { productVM.addOrUpdate() },
            onCancel = { productVM.clearForm() },
            onEdit = { id -> productVM.edit(id) },
            onDelete = { id -> productVM.delete(id) },
            onBack = { screen = Screen.HOME }
        )
    }
}

