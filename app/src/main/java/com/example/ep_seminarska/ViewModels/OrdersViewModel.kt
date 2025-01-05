package com.example.ep_seminarska.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ep_seminarska.APICartItem
import com.example.ep_seminarska.Order
import com.example.ep_seminarska.OrdersService
import com.example.ep_seminarska.PlaceOrderRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class OrderManagementState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String? = null,
    val orderPlacementSuccess: Boolean = false,
    val lastPlacedOrderId: Int? = null
)

class OrderManagementViewModel : ViewModel() {
    private val _orderState = MutableStateFlow(OrderManagementState())
    val orderState: StateFlow<OrderManagementState> = _orderState

    private val ordersService: OrdersService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        ordersService = retrofit.create(OrdersService::class.java)
    }

    fun fetchOrders(userId: String) {
        viewModelScope.launch {
            _orderState.value = _orderState.value.copy(isLoading = true, error = null)
            try {
                val response = ordersService.getOrderHistory("api", "getOrderHistory", userId)
                if (response.status == "success") {
                    _orderState.value = _orderState.value.copy(
                        isLoading = false,
                        orders = response.data.orders,
                        error = null
                    )
                } else {
                    _orderState.value = _orderState.value.copy(
                        isLoading = false,
                        error = "Failed to fetch orders"
                    )
                }
            } catch (e: Exception) {
                _orderState.value = _orderState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun placeOrder(
        userId: Int,
        shippingAddress: String,
        postalCode: String,
        city: String,
        phone: String,
        notes: String,
        cartItems: List<APICartItem>
    ) {
        viewModelScope.launch {
            _orderState.value = _orderState.value.copy(
                isLoading = true,
                error = null,
                orderPlacementSuccess = false
            )

            try {
                val orderRequest = PlaceOrderRequest(
                    user_id = userId,
                    shipping_address = shippingAddress,
                    postal_code = postalCode,
                    city = city,
                    phone = phone,
                    notes = notes,
                    cart_items = cartItems
                )

                val response = ordersService.placeOrder("api", "placeOrder", orderRequest)

                if (response.status == "success") {
                    _orderState.value = _orderState.value.copy(
                        isLoading = false,
                        error = null,
                        orderPlacementSuccess = true,
                        lastPlacedOrderId = response.data.order_id
                    )

                    // Refresh the orders list after successful placement
                    fetchOrders(userId.toString())
                } else {
                    _orderState.value = _orderState.value.copy(
                        isLoading = false,
                        error = "Failed to place order",
                        orderPlacementSuccess = false
                    )
                }
            } catch (e: Exception) {
                _orderState.value = _orderState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred",
                    orderPlacementSuccess = false
                )
            }
        }
    }

    fun resetOrderPlacement() {
        _orderState.value = _orderState.value.copy(
            orderPlacementSuccess = false,
            lastPlacedOrderId = null,
            error = null
        )
    }
}