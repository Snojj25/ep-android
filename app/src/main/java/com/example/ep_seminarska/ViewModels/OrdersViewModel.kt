package com.example.ep_seminarska.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ep_seminarska.Order
import com.example.ep_seminarska.OrdersService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class OrdersState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String? = null
)

class OrdersViewModel : ViewModel() {
    private val _ordersState = MutableStateFlow(OrdersState())
    val ordersState: StateFlow<OrdersState> = _ordersState

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
            _ordersState.value = _ordersState.value.copy(isLoading = true, error = null)
            try {
                val response = ordersService.getOrderHistory("api", "getOrderHistory", userId)
                if (response.status == "success") {
                    _ordersState.value = _ordersState.value.copy(
                        isLoading = false,
                        orders = response.data.orders,
                        error = null
                    )
                } else {
                    _ordersState.value = _ordersState.value.copy(
                        isLoading = false,
                        error = "Failed to fetch orders"
                    )
                }
            } catch (e: Exception) {
                _ordersState.value = _ordersState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
}