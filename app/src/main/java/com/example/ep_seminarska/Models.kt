package com.example.ep_seminarska

import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

data class ApiResponse(
    val status: String,
    val data: ApiData
)

data class ApiData(
    val products: List<Product>,
)

data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Float,
    val stock: Int,
    val is_active: Int,
    val created_at: String,
    val updated_at: String,
    val imageBase64: String
)


interface ApiService {
    @GET("index.php?controller=api")
    suspend fun getProducts(): ApiResponse

}
//@Query("controller") controller: String


data  class ApiResponseAuth(
    val status: String,
    val data: ApiDataAuth
)

data class ApiDataAuth(
    val authenticated: Boolean,
    val user: User?
)

data class User(
    val id: Int,
    val first_name: String,
    val last_name: String?,
    val email: String,
    val role: String,
    val address: String,
    val postal_code: String,
    val city: String,
    val is_active: Boolean,
    val created_at: String,
    val updated_at: String
)

// Interface defining the Auth endpoints
interface AuthService {
    @GET("index.php?controller=api&action=authenticate")
    suspend fun authenticate(@Query("email") email: String, @Query("password") password: String ): ApiResponseAuth
}


data  class ApiResponseOrders(
    val status: String,
    val data: ApiDataOrders
)

data class ApiDataOrders(
    val orders: List<Order>
)

data class Order(
    val id: Int,
    val user_id: Int,
    val status: String?,
    val total_amount: String,
    val created_at: String,
    val updated_at: String,
    val shipping_address: String,
    val postal_code: String,
    val city: Boolean,
    val phone: String,
    val notes: String,
    val items: List<OrderItem>
)

data class OrderItem(
    val id: Int,
    val order_id: Int,
    val product_id: Int,
    val quantity: Int,
    val price: String,
    val product_name: String,
)



// Request related data classes
data class APICartItem(
    val product_id: Int,
    val name: String,
    val price: Float,
    val quantity: Int
)

data class PlaceOrderRequest(
    val user_id: Int,
    val shipping_address: String,
    val postal_code: String,
    val city: String,
    val phone: String,
    val notes: String,
    val cart_items: List<APICartItem>
)

// Response related data classes
data class PlaceOrderResponse(
    val status: String,
    val data: PlaceOrderData
)

data class PlaceOrderData(
    val order_id: String
)


// Interface defining the OrderHistory endpoints
interface OrdersService {
    @GET("index.php?controller=api&action=getOrderHistory")
    suspend fun getOrderHistory(@Query("userId") userId: String ): ApiResponseOrders

    @POST("index.php?controller=api&action=placeOrder")
    @Headers("Content-Type: application/json")
    suspend fun placeOrder(
        @Body requestBody: RequestBody
    ): PlaceOrderResponse
}






