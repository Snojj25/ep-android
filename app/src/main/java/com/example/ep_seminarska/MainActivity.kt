package ep.seminarska

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ep_seminarska.ApiService
import com.example.ep_seminarska.ViewModels.AuthViewModel
import com.example.ep_seminarska.ui.screens.LoginScreen
import com.example.ep_seminarska.Product
import com.example.ep_seminarska.ViewModels.OrdersViewModel
import com.example.ep_seminarska.ui.screens.ProductDetailScreen
import com.example.ep_seminarska.ui.screens.ProductListScreen
import com.example.ep_seminarska.ViewModels.ProductViewModel
import com.example.ep_seminarska.ui.screens.OrderHistoryScreen
import com.example.ep_seminarska.ui.screens.ProfileScreen


class MainActivity : ComponentActivity() {
    private val tag = this::class.java.canonicalName
    private lateinit var apiService: ApiService

    private var products: List<Product> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val productViewModel = viewModel<ProductViewModel>()
                    val productsState by productViewModel.productsState.collectAsState()

                    if (productsState.isLoading) {
                        // Show loading indicator
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (productsState.error != null) {
                        // Show error state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(productsState.error!!)
                        }
                    } else {
                        // Show content
                        AppNavigation(productsState.products)
                    }
                }
            }
        }

    }



}

@Composable
fun AppNavigation(products: List<Product>) {
    val navController = rememberNavController()
    val authViewModel = remember { AuthViewModel() }
    val ordersViewModel = remember { OrdersViewModel() }

    NavHost(navController = navController, startDestination = "products") {
        composable("products") {
            //HomeScreen(navController, products)
            ProductListScreen(navController, products, authViewModel)
        }
        composable("productDetail/{productId}") {backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
            val product = products.find { it.id == productId }
            if (product != null) {
               ProductDetailScreen(navController, product)
            }
        }
        composable("login") {
            LoginScreen(authViewModel, navController)
        }
        composable("profile") {
            ProfileScreen(authViewModel, navController)
        }
        composable("orders") {
            OrderHistoryScreen(
                ordersViewModel = ordersViewModel,
                authViewModel = authViewModel,
                navController = navController
            )
        }
    }
}
