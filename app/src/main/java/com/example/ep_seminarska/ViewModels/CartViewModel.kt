package com.example.ep_seminarska.ViewModels

import com.example.ep_seminarska.Product
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class CartItem(
    val product: Product,
    val quantity: Int
)

data class CartState(
    val items: List<CartItem> = emptyList(),
    val shippingAddress: String = "",
    val postalCode: String = "",
    val city: String = "",
    val phone: String = "",
    val notes: String = ""
)

class CartViewModel : ViewModel() {
    private val _cartState = MutableStateFlow(CartState())
    val cartState: StateFlow<CartState> = _cartState

    fun addToCart(product: Product) {
        _cartState.update { currentState ->
            val currentItems = currentState.items.toMutableList()
            val existingItem = currentItems.find { it.product.id == product.id }

            if (existingItem != null) {
                val index = currentItems.indexOf(existingItem)
                currentItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
            } else {
                currentItems.add(CartItem(product, 1))
            }

            currentState.copy(items = currentItems)
        }
    }

    fun updateQuantity(productId: Int, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }

        _cartState.update { currentState ->
            val currentItems = currentState.items.toMutableList()
            val index = currentItems.indexOfFirst { it.product.id == productId }
            if (index != -1) {
                currentItems[index] = currentItems[index].copy(quantity = quantity)
            }
            currentState.copy(items = currentItems)
        }
    }

    fun removeFromCart(productId: Int) {
        _cartState.update { currentState ->
            currentState.copy(
                items = currentState.items.filter { it.product.id != productId }
            )
        }
    }

    fun updateShippingDetails(
        address: String? = null,
        postalCode: String? = null,
        city: String? = null,
        phone: String? = null,
        notes: String? = null
    ) {
        _cartState.update { currentState ->
            currentState.copy(
                shippingAddress = address ?: currentState.shippingAddress,
                postalCode = postalCode ?: currentState.postalCode,
                city = city ?: currentState.city,
                phone = phone ?: currentState.phone,
                notes = notes ?: currentState.notes
            )
        }
    }

    fun clearCart() {
        _cartState.value = CartState()
    }

    fun getTotal(): Float {
        return _cartState.value.items.sumOf {
            (it.product.price * it.quantity).toDouble()
        }.toFloat()
    }
}