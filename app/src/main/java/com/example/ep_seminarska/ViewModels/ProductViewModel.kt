package com.example.ep_seminarska.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ep_seminarska.ApiService
import com.example.ep_seminarska.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductViewModel : ViewModel() {
    private val _productsState = MutableStateFlow<ProductsState>(ProductsState())
    val productsState: StateFlow<ProductsState> = _productsState

    private val apiService: ApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
        fetchProducts()
    }

    data class ProductsState(
        val products: List<Product> = listOf(),
        val isLoading: Boolean = true,
        val error: String? = null
    )

    private fun fetchProducts() {
        viewModelScope.launch {
            try {
                val response = apiService.getProducts()
                if (response.status == "success") {
                    _productsState.value = ProductsState(
                        products = response.data.products,
                        isLoading = false
                    )
                } else {
                    _productsState.value = ProductsState(
                        isLoading = false,
                        error = "Error: API returned failure status"
                    )
                }
            } catch (e: Exception) {
                _productsState.value = ProductsState(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
}