package com.example.sakafo.ui.screen.cart


import androidx.compose.runtime.mutableStateListOf
import com.example.sakafo.data.Dish

// ── Modèle ────────────────────────────────────────────────────────────────────
data class CartItem(
    val dish: Dish,
    var quantity: Int
)

// ── Singleton global du panier ────────────────────────────────────────────────
object CartManager {

    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    /** Ajoute 1 unité — incrémente si déjà présent */
    fun addItem(dish: Dish) {
        val existing = _cartItems.find { it.dish.id == dish.id }
        if (existing != null) existing.quantity++
        else _cartItems.add(CartItem(dish, 1))
    }

    /** Supprime complètement un plat du panier */
    fun removeItem(dishId: String) {
        _cartItems.removeAll { it.dish.id == dishId }
    }

    /** Met à jour la quantité — supprime si quantité ≤ 0 */
    fun updateQuantity(dishId: String, quantity: Int) {
        val item = _cartItems.find { it.dish.id == dishId }
        if (item != null) {
            if (quantity > 0) item.quantity = quantity
            else removeItem(dishId)
        }
    }

    /** Diminue de 1 — supprime si quantité tombe à 0 */
    fun decreaseQuantity(dishId: String) {
        val item = _cartItems.find { it.dish.id == dishId }
        if (item != null) {
            if (item.quantity > 1) item.quantity--
            else removeItem(dishId)
        }
    }

    fun getTotalPrice(): Double = _cartItems.sumOf { it.dish.price * it.quantity }

    fun getItemCount(): Int = _cartItems.sumOf { it.quantity }

    fun clearCart() = _cartItems.clear()
}