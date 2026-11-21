package com.example.foodorder.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.foodorder.data.Food
import com.example.foodorder.data.Pricing

data class CartItem(
    val food: Food,
    val quantity: Int,
    val note: String = ""
) {
    val total: Int get() = food.price * quantity
}

/**
 * Pusat state aplikasi (keranjang + favorit).
 * Memakai mutableStateListOf supaya Compose otomatis recompose saat isi list berubah.
 */
class FoodViewModel : ViewModel() {

    private val _cart = mutableStateListOf<CartItem>()
    val cart: List<CartItem> get() = _cart

    private val _favorites = mutableStateListOf<Int>()

    val totalItems: Int get() = _cart.sumOf { it.quantity }
    val subtotal: Int get() = _cart.sumOf { it.total }

    // ---------- Favorit ----------
    fun isFavorite(foodId: Int): Boolean = foodId in _favorites

    fun toggleFavorite(foodId: Int) {
        if (!_favorites.remove(foodId)) _favorites.add(foodId)
    }

    // ---------- Keranjang ----------
    fun addToCart(food: Food, quantity: Int = 1, note: String = "") {
        val index = _cart.indexOfFirst { it.food.id == food.id }
        if (index >= 0) {
            val old = _cart[index]
            _cart[index] = old.copy(
                quantity = (old.quantity + quantity).coerceAtMost(Pricing.MAX_QUANTITY),
                note = note.trim().ifBlank { old.note }
            )
        } else {
            _cart.add(CartItem(food, quantity.coerceIn(1, Pricing.MAX_QUANTITY), note.trim()))
        }
    }

    fun changeQuantity(foodId: Int, delta: Int) {
        val index = _cart.indexOfFirst { it.food.id == foodId }
        if (index < 0) return
        val newQty = (_cart[index].quantity + delta).coerceIn(1, Pricing.MAX_QUANTITY)
        _cart[index] = _cart[index].copy(quantity = newQty)
    }

    fun removeFromCart(foodId: Int) {
        _cart.removeAll { it.food.id == foodId }
    }

    /** Dipakai untuk tombol "Urungkan" setelah item dihapus. */
    fun restoreToCart(item: CartItem, index: Int) {
        if (_cart.none { it.food.id == item.food.id }) {
            _cart.add(index.coerceIn(0, _cart.size), item)
        }
    }

    fun clearCart() = _cart.clear()

    /** Membuat pesanan: mengosongkan keranjang dan mengembalikan nomor pesanan. */
    fun placeOrder(): String {
        val code = "FO-" + (1000..9999).random()
        _cart.clear()
        return code
    }
}
