package com.example.tiendastore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendastore.data.DataBaseHelper
import com.example.tiendastore.data.toDomain
import com.example.tiendastore.data.toEntity
import com.example.tiendastore.model.CartItem
import com.example.tiendastore.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val app = getApplication<Application>()

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    val totalItems: StateFlow<Int> = MutableStateFlow(0).apply {
        viewModelScope.launch {
            _items.collectLatest { value = it.sumOf { it.qty } }
        }
    }

    val totalPrice: StateFlow<Double> = MutableStateFlow(0.0).apply {
        viewModelScope.launch {
            _items.collectLatest { value = it.sumOf { it.price * it.qty } }
        }
    }

    init {
        viewModelScope.launch {
            DataBaseHelper.db(app).cartDao().observeAll().collectLatest { entities ->
                _items.value = entities.map { it.toDomain() }
            }
        }
    }

    fun add(product: Product, qty: Int = 1) {
        viewModelScope.launch {
            val dao = DataBaseHelper.db(app).cartDao()
            val existing = dao.getByIdOnce(product.id)
            if (existing != null) {
                dao.updateQty(product.id, (existing.qty + qty).coerceAtLeast(1))
            } else {
                dao.upsert(CartItem(product.id, product.name, product.price, qty.coerceAtLeast(1), product.imagePath).toEntity())
            }
        }
    }

    fun changeQty(productId: Int, qty: Int) {
        if (qty <= 0) return
        viewModelScope.launch {
            DataBaseHelper.db(app).cartDao().updateQty(productId, qty)
        }
    }

    fun remove(productId: Int) {
        viewModelScope.launch {
            DataBaseHelper.db(app).cartDao().deleteById(productId)
        }
    }

    fun clear() {
        viewModelScope.launch { DataBaseHelper.db(app).cartDao().clear() }
    }
}
