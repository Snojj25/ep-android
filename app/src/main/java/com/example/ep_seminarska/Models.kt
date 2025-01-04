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
    val productImages: List<ProductImage>
)

data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Float,
    val stock: Int,
    val is_active: Int,
    val created_at: String,
    val updated_at: String
)

data class ProductImage(
    @SerializedName("id") val id: Int,
    @SerializedName("productId") val productId: Int,
    @SerializedName("filePath") val filePath: String,
    @SerializedName("isPrimary") val isPrimary: Boolean
)



// Interface defining the API endpoints
interface ApiService {
    @GET("index.php")
    suspend fun getProducts(@Query("controller") controller: String): ApiResponse
}