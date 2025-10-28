package com.example.tiendastore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import com.example.tiendastore.data.DataBaseHelper
import com.example.tiendastore.data.toDomain
import com.example.tiendastore.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.tiendastore.domain.validation.ProductValidator

data class ProductFormState(
    val id: Int? = null,
    val name: String = "",
    val price: String = "",
    val stock: String = "",
    val category: String = "Consolas",
    val description: String = "",
    val imagePath: String = "",
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
            DataBaseHelper.db(appContext).productDao().observeAll().collectLatest { entities ->
                _products.value = entities.map { it.toDomain() }
            }
        }
        viewModelScope.launch { loadSeedIfEmpty() }
    }

    suspend fun loadSeedIfEmpty() {
        val dao = DataBaseHelper.db(appContext).productDao()
        val list = dao.observeAll().first()
        if (list.isEmpty()) {
            val seed = listOf(
                Product(0, "Consola X1", 299990.0, 5, "Consolas", "Consola de última generación"),
                Product(0, "Juego Aventura", 39990.0, 10, "Juegos", "Gran aventura en mundo abierto"),
                Product(0, "Control Pro", 49990.0, 0, "Accesorios", "Control inalámbrico"),
                Product(0, "Auriculares Gamer", 29990.0, 8, "Accesorios", "Con micrófono"),
                Product(0, "Tarjeta Regalo", 10000.0, 20, "Otros", "Crédito para tienda")
            )
            seed.forEach { p -> DataBaseHelper.upsertProductWithOptionalImage(appContext, p, null) }
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
            "imagePath" -> current.copy(imagePath = value)
            else -> current
        }
        _form.value = validate(updated)
    }

    private fun validate(form: ProductFormState): ProductFormState {
        val errors = ProductValidator.validate(form.name, form.price, form.stock, form.category)
        return form.copy(errors = errors, isValid = errors.isEmpty())
    }

    fun addOrUpdate() {
        viewModelScope.launch {
            val f = _form.value
            val vf = validate(f)
            _form.value = vf
            if (!vf.isValid) return@launch

            val prod = Product(
                id = vf.id ?: 0,
                name = vf.name.trim(),
                price = vf.price.replace(",", ".").toDouble(),
                stock = vf.stock.toInt(),
                category = vf.category,
                description = vf.description.trim(),
                imagePath = if (vf.imagePath.startsWith("/")) vf.imagePath else null
            )
            val maybeUri = vf.imagePath.takeIf { it.startsWith("content:") }?.let { Uri.parse(it) }
            DataBaseHelper.upsertProductWithOptionalImage(appContext, prod, maybeUri)
            clearForm()
        }
    }

    fun edit(id: Int) {
        viewModelScope.launch {
            val entity = DataBaseHelper.db(appContext).productDao().getByIdOnce(id) ?: return@launch
            _form.value = ProductFormState(
                id = entity.id,
                name = entity.name,
                price = entity.price.toString(),
                stock = entity.stock.toString(),
                category = entity.category,
                description = entity.description,
                imagePath = entity.imagePath.orEmpty(),
                errors = emptyMap(),
                isValid = true
            )
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            DataBaseHelper.deleteProduct(appContext, id)
        }
    }

    fun clearForm() {
        _form.value = ProductFormState()
    }
}
