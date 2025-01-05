package com.example.ep_seminarska.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ep_seminarska.Order
import com.example.ep_seminarska.OrderItem
import com.example.ep_seminarska.ViewModels.OrderManagementViewModel
import com.example.ep_seminarska.ViewModels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    ordersViewModel: OrderManagementViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val ordersState by ordersViewModel.orderState.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState.user) {
        if (authState.user == null) {
            navController.navigate("login") {
                popUpTo("orders") { inclusive = true }
            }
        } else {
            ordersViewModel.fetchOrders(authState.user!!.id.toString())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order History") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("products") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "home",
                            modifier = Modifier
                                .fillMaxSize()
                                .size(16.dp),
                        )

                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            ordersState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            ordersState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ordersState.error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            ordersState.orders.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No orders found")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(ordersState.orders) { order ->
                        OrderCard(order = order)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCard(order: Order) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                        contentDescription = if (expanded) "Show less" else "Show more"
                    )
                }
            }

            Text(
                text = "Status: ${order.status ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Total: $${order.total_amount}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Date: ${order.created_at}",
                style = MaterialTheme.typography.bodySmall
            )

            if (expanded) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Items:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                order.items.forEach { item ->
                    OrderItemRow(item = item)
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Shipping Details",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(text = "Address: ${order.shipping_address}")
                Text(text = "Postal Code: ${order.postal_code}")
                Text(text = "Phone: ${order.phone}")
                if (order.notes.isNotEmpty()) {
                    Text(text = "Notes: ${order.notes}")
                }
            }
        }
    }
}

@Composable
fun OrderItemRow(item: OrderItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "${item.quantity}x ${item.product_name}",
            modifier = Modifier.weight(1f)
        )
        Text(text = "$${item.price}")
    }
}