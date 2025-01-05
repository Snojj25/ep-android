package com.example.ep_seminarska.ui.screens





import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ep_seminarska.ViewModels.AuthViewModel
import com.example.ep_seminarska.ViewModels.CartItem
import com.example.ep_seminarska.ViewModels.CartState
import com.example.ep_seminarska.ViewModels.CartViewModel
import com.example.ep_seminarska.ViewModels.OrderManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    orderViewModel: OrderManagementViewModel,
    authViewModel: AuthViewModel
) {
    val cartState by cartViewModel.cartState.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val orderState by orderViewModel.orderState.collectAsState()

    var showConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(orderState.orderPlacementSuccess) {
        if (orderState.orderPlacementSuccess) {
            cartViewModel.clearCart()
            showConfirmation = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping Cart") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (showConfirmation) {
            OrderConfirmationDialog(
                onDismiss = {
                    showConfirmation = false
                    navController.navigate("orders") {
                        popUpTo("cart") { inclusive = true }
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            if (cartState.items.isEmpty()) {
                EmptyCartMessage(
                    onContinueShopping = { navController.navigateUp() }
                )
            } else {
                CartItemsList(
                    items = cartState.items,
                    onUpdateQuantity = { productId, quantity ->
                        cartViewModel.updateQuantity(productId, quantity)
                    },
                    onRemoveItem = { productId ->
                        cartViewModel.removeFromCart(productId)
                    }
                )

                ShippingDetailsForm(
                    cartState = cartState,
                    onUpdateDetails = cartViewModel::updateShippingDetails
                )

                OrderSummary(
                    total = cartViewModel.getTotal(),
                    onPlaceOrder = {
                        if (authState.user != null) {
                            orderViewModel.placeOrder(
                                userId = authState.user!!.id,
                                shippingAddress = cartState.shippingAddress,
                                postalCode = cartState.postalCode,
                                city = cartState.city,
                                phone = cartState.phone,
                                notes = cartState.notes,
                                cartItems = cartState.items.map {
                                    com.example.ep_seminarska.APICartItem(
                                        product_id = it.product.id,
                                        name = it.product.name,
                                        price = it.product.price,
                                        quantity = it.quantity
                                    )
                                }
                            )
                        }
                    },
                    isEnabled = authState.user != null &&
                            cartState.shippingAddress.isNotBlank() &&
                            cartState.postalCode.isNotBlank() &&
                            cartState.city.isNotBlank() &&
                            cartState.phone.isNotBlank()
                )
            }
        }
    }
}

@Composable
fun CartItemsList(
    items: List<CartItem>,
    onUpdateQuantity: (Int, Int) -> Unit,
    onRemoveItem: (Int) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        items.forEach { item ->
            CartItemRow(
                item = item,
                onUpdateQuantity = { quantity ->
                    onUpdateQuantity(item.product.id, quantity)
                },
                onRemove = { onRemoveItem(item.product.id) }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onUpdateQuantity: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.product.name, style = MaterialTheme.typography.titleMedium)
            Text(
                "€%.2f".format(item.product.price),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { onUpdateQuantity(item.quantity - 1) }) {
                Icon(Icons.Default.KeyboardArrowDown, "Decrease")
            }
            Text(item.quantity.toString())
            IconButton(onClick = { onUpdateQuantity(item.quantity + 1) }) {
                Icon(Icons.Default.Add, "Increase")
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, "Remove")
            }
        }
    }
}

@Composable
fun ShippingDetailsForm(
    cartState: CartState,
    onUpdateDetails: (String?, String?, String?, String?, String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Shipping Details",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = cartState.shippingAddress,
            onValueChange = { onUpdateDetails(it, null, null, null, null) },
            label = { Text("Shipping Address") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cartState.postalCode,
            onValueChange = { onUpdateDetails(null, it, null, null, null) },
            label = { Text("Postal Code") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cartState.city,
            onValueChange = { onUpdateDetails(null, null, it, null, null) },
            label = { Text("City") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cartState.phone,
            onValueChange = { onUpdateDetails(null, null, null, it, null) },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cartState.notes,
            onValueChange = { onUpdateDetails(null, null, null, null, it) },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun OrderSummary(
    total: Float,
    onPlaceOrder: () -> Unit,
    isEnabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Total:",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                "€%.2f".format(total),
                style = MaterialTheme.typography.titleLarge
            )
        }

        Button(
            onClick = onPlaceOrder,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = isEnabled
        ) {
            Text("Place Order")
        }
    }
}

@Composable
fun EmptyCartMessage(onContinueShopping: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Your cart is empty",
            style = MaterialTheme.typography.titleLarge
        )
        Button(
            onClick = onContinueShopping,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Continue Shopping")
        }
    }
}

@Composable
fun OrderConfirmationDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Order Placed Successfully") },
        text = { Text("Your order has been placed successfully. You can view your order details in the Orders section.") },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("View Orders")
            }
        }
    )
}