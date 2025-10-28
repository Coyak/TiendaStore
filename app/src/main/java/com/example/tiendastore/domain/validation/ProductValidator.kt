package com.example.tiendastore.domain.validation

object ProductValidator {
    private val categories = setOf("Consolas", "Juegos", "Accesorios", "Otros")

    fun validate(
        name: String,
        priceText: String,
        stockText: String,
        category: String
    ): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        if (name.trim().length < 3) errors["name"] = "Nombre mínimo 3"

        val priceVal = priceText.replace(",", ".").toDoubleOrNull()
        if (priceVal == null || priceVal <= 0.0) errors["price"] = "Precio > 0"

        val stockVal = stockText.toIntOrNull()
        if (stockVal == null || stockVal < 0) errors["stock"] = "Stock ≥ 0"

        if (!categories.contains(category)) errors["category"] = "Categoría inválida"
        return errors
    }
}

