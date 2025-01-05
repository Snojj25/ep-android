package com.example.ep_seminarska

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
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
    @GET("index.php")
    suspend fun getProducts(@Query("controller") controller: String): ApiResponse

}


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
    @GET("index.php")
    suspend fun authenticate(@Query("controller") controller: String, @Query("action") action: String, @Query("email") email: String, @Query("password") password: String ): ApiResponseAuth
}