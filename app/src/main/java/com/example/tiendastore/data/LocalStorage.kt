package com.example.tiendastore.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.tiendastore.model.Product
import com.example.tiendastore.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

val Context.dataStore by preferencesDataStore(name = "tiendastore")

object LocalStorage {
    private val KEY_USERS = stringPreferencesKey("users_json")
    private val KEY_CURRENT_USER = stringPreferencesKey("current_user_json")
    private val KEY_PRODUCTS = stringPreferencesKey("products_json")

    fun usersFlow(context: Context): Flow<List<User>> =
        context.dataStore.data.map { prefs ->
            val json = prefs[KEY_USERS] ?: "[]"
            runCatching { parseUsers(json) }.getOrElse { emptyList() }
        }

    suspend fun saveUsers(context: Context, users: List<User>) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USERS] = usersToJson(users)
        }
    }

    fun currentUserFlow(context: Context): Flow<User?> =
        context.dataStore.data.map { prefs ->
            val json = prefs[KEY_CURRENT_USER] ?: ""
            if (json.isBlank()) return@map null
            runCatching {
                val obj = JSONObject(json)
                val username = obj.optString("username", "")
                if (username.isBlank()) null else User(
                    username = username,
                    password = "",
                    isAdmin = obj.optBoolean("isAdmin", false)
                )
            }.getOrNull()
        }

    suspend fun setCurrentUser(context: Context, user: User?) {
        context.dataStore.edit { prefs ->
            if (user == null) {
                prefs[KEY_CURRENT_USER] = ""
            } else {
                val obj = JSONObject()
                obj.put("username", user.username)
                obj.put("isAdmin", user.isAdmin)
                prefs[KEY_CURRENT_USER] = obj.toString()
            }
        }
    }

    fun productsFlow(context: Context): Flow<List<Product>> =
        context.dataStore.data.map { prefs ->
            val json = prefs[KEY_PRODUCTS] ?: "[]"
            runCatching { parseProducts(json) }.getOrElse { emptyList() }
        }

    suspend fun saveProducts(context: Context, products: List<Product>) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PRODUCTS] = productsToJson(products)
        }
    }

    // JSON helpers
    fun parseUsers(json: String): List<User> {
        if (json.isBlank()) return emptyList()
        val arr = JSONArray(json)
        val list = mutableListOf<User>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val username = obj.optString("username", "")
            val password = obj.optString("password", "")
            val isAdmin = obj.optBoolean("isAdmin", false)
            if (username.isNotBlank()) {
                list.add(User(username, password, isAdmin))
            }
        }
        return list
    }

    fun usersToJson(users: List<User>): String {
        val arr = JSONArray()
        users.forEach { u ->
            val obj = JSONObject()
            obj.put("username", u.username)
            obj.put("password", u.password)
            obj.put("isAdmin", u.isAdmin)
            arr.put(obj)
        }
        return arr.toString()
    }

    fun parseProducts(json: String): List<Product> {
        if (json.isBlank()) return emptyList()
        val arr = JSONArray(json)
        val list = mutableListOf<Product>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(
                Product(
                    id = obj.optInt("id", 0),
                    name = obj.optString("name", ""),
                    price = obj.optDouble("price", 0.0),
                    stock = obj.optInt("stock", 0),
                    category = obj.optString("category", "Otros"),
                    description = obj.optString("description", "")
                )
            )
        }
        return list
    }

    fun productsToJson(products: List<Product>): String {
        val arr = JSONArray()
        products.forEach { p ->
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("name", p.name)
            obj.put("price", p.price)
            obj.put("stock", p.stock)
            obj.put("category", p.category)
            obj.put("description", p.description)
            arr.put(obj)
        }
        return arr.toString()
    }
}
