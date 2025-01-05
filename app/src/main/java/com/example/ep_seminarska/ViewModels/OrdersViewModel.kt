package com.example.ep_seminarska.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ep_seminarska.APICartItem
import com.example.ep_seminarska.Order
import com.example.ep_seminarska.OrdersService
import com.example.ep_seminarska.PlaceOrderRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class OrderManagementState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String? = null,
    val orderPlacementSuccess: Boolean = false,
    val lastPlacedOrderId: String? = null
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
                val response = ordersService.getOrderHistory(userId)
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

        Log.i("HERE IN PLACE ORDER: ", userId.toString())

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

                val jsonBody = Gson().toJson(orderRequest)
                val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())


                Log.i("BEFORE PLACE ORDER: ", orderRequest.toString())
                val response = ordersService.placeOrder(requestBody)
                Log.i("PLACE ORDER RES: ", response.toString())

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