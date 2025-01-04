package ep.seminarska

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import androidx.activity.ComponentActivity
import com.example.ep_seminarska.ApiService
import com.example.ep_seminarska.Product
import com.example.ep_seminarska.ProductDetailScreen
import com.example.ep_seminarska.ProductListScreen




class MainActivity : ComponentActivity() {
    private val tag = this::class.java.canonicalName
    private lateinit var apiService: ApiService

    private var products: List<Product> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupRetrofit()

        fetchProducts()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(products)
                }
            }
        }

    }

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000")  // Replace with your computer's IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        apiService = retrofit.create(ApiService::class.java)
    }

    private fun fetchProducts() {
        lifecycleScope.launch {
            try {
                val response = apiService.getProducts("api")
                if (response.status == "success") {
                    products = response.data.products
                } else {
                    Log.e(tag, "Error: API returned failure status")
                }
            } catch (e: Exception) {
                Log.e(tag, "Error fetching data", e)
            }
        }
    }

}

@Composable
fun AppNavigation(products: List<Product>) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "products") {
        composable("products") {
            //HomeScreen(navController, products)
            ProductListScreen(navController, products)
        }
        composable("productDetail/{productId}") {backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
            val product = products.find { it.id == productId }
            if (product != null) {
               ProductDetailScreen(navController, product)
            }
        }
    }
}


@Composable
fun DetailScreen(id: String, navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Detail Screen for item $id")
        Button(onClick = { navController.navigateUp() }) {
            Text("Go Back")
        }
    }
}