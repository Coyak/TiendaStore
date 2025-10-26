package com.example.tiendastore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendastore.data.LocalStorage
import com.example.tiendastore.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ProductFormState(
    val id: Int? = null,
    val name: String = "",
    val price: String = "",
    val stock: String = "",
    val category: String = "Consolas",
    val description: String = "",
    val errors: Map<String, String> = emptyMap(),
    val isValid: Boolean = false
)

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = getApplication<Application>()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _form = MutableStateFlow(ProductFormState())
    val form: StateFlow<ProductFormState> = _form.asStateFlow()

    private val categories = listOf("Consolas", "Juegos", "Accesorios", "Otros")

    init {
        viewModelScope.launch {
            LocalStorage.productsFlow(appContext).collectLatest { list ->
                _products.value = list
            }
        }
        viewModelScope.launch { loadSeedIfEmpty() }
    }

    suspend fun loadSeedIfEmpty() {
        val list = LocalStorage.productsFlow(appContext).first()
        if (list.isEmpty()) {
            val seed = listOf(
                Product(1, "Consola X1", 299990.0, 5, "Consolas", "Consola de última generación"),
                Product(2, "Juego Aventura", 39990.0, 10, "Juegos", "Gran aventura en mundo abierto"),
                Product(3, "Control Pro", 49990.0, 0, "Accesorios", "Control inalámbrico"),
                Product(4, "Auriculares Gamer", 29990.0, 8, "Accesorios", "Con micrófono"),
                Product(5, "Tarjeta Regalo", 10000.0, 20, "Otros", "Crédito para tienda")
            )
            LocalStorage.saveProducts(appContext, seed)
        }
    }

    fun onFieldChange(field: String, value: String) {
        val current = _form.value
        val updated = when (field) {
            "name" -> current.copy(name = value)
            "price" -> current.copy(price = value)
            "stock" -> current.copy(stock = value)
            "category" -> current.copy(category = value)
            "description" -> current.copy(description = value)
            else -> current
        }
        _form.value = validate(updated)
    }

    private fun validate(form: ProductFormState): ProductFormState {
        val errors = mutableMapOf<String, String>()

        if (form.name.trim().length < 3) errors["name"] = "Nombre mínimo 3"

        val price = form.price.replace(",", ".")
        val priceVal = price.toDoubleOrNull()
        if (priceVal == null || priceVal <= 0.0) errors["price"] = "Precio > 0"

        val stockVal = form.stock.toIntOrNull()
        if (stockVal == null || stockVal < 0) errors["stock"] = "Stock ≥ 0"

        if (!categories.contains(form.category)) errors["category"] = "Categoría inválida"

        return form.copy(errors = errors, isValid = errors.isEmpty())
    }

    fun addOrUpdate() {
        viewModelScope.launch {
            val f = _form.value
            val vf = validate(f)
            _form.value = vf
            if (!vf.isValid) return@launch

            val current = LocalStorage.productsFlow(appContext).first()
            val nextList = if (vf.id == null) {
                val nextId = (current.maxOfOrNull { it.id } ?: 0) + 1
                current + Product(
                    id = nextId,
                    name = vf.name.trim(),
                    price = vf.price.replace(",", ".").toDouble(),
                    stock = vf.stock.toInt(),
                    category = vf.category,
                    description = vf.description.trim()
                )
            } else {
                current.map {
                    if (it.id == vf.id) it.copy(
                        name = vf.name.trim(),
                        price = vf.price.replace(",", ".").toDouble(),
                        stock = vf.stock.toInt(),
                        category = vf.category,
                        description = vf.description.trim()
                    ) else it
                }
            }
            LocalStorage.saveProducts(appContext, nextList)
            clearForm()
        }
    }

    fun edit(id: Int) {
        viewModelScope.launch {
            val current = LocalStorage.productsFlow(appContext).first()
            val item = current.firstOrNull { it.id == id } ?: return@launch
            _form.value = ProductFormState(
                id = item.id,
                name = item.name,
                price = item.price.toString(),
                stock = item.stock.toString(),
                category = item.category,
                description = item.description,
                errors = emptyMap(),
                isValid = true
            )
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            val current = LocalStorage.productsFlow(appContext).first()
            val next = current.filterNot { it.id == id }
            LocalStorage.saveProducts(appContext, next)
        }
    }

    fun clearForm() {
        _form.value = ProductFormState()
    }
}

