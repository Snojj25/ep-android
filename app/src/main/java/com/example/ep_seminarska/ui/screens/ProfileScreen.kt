package com.example.ep_seminarska.ui.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ep_seminarska.ViewModels.AuthViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    navController: NavController
) {
    val authState by viewModel.authState.collectAsState()
    val user = authState.user

    LaunchedEffect(user) {
        if (user == null) {
            navController.navigate("login") {
                popUpTo("profile") { inclusive = true }
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", modifier = Modifier
                    .fillMaxSize()
                    .size(16.dp)
                    .padding(16.dp) ) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("products") {
                                popUpTo("profile") { inclusive = true }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                user?.let {
                    ProfileItem("Name", "${it.first_name} ${it.last_name ?: ""}")
                    ProfileItem("Email", it.email.toString())
                    ProfileItem("Address", it.address)
                    ProfileItem("City", it.city)
                    ProfileItem("Postal Code", it.postal_code)
                }

                Button(
                    onClick = { navController.navigate("orders") {
                        popUpTo("profile") { inclusive = true }
                    } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text("Order History")
                }
                
                Spacer(modifier = Modifier.size(20.dp))
                
                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text("Logout")
                }
            }
    }
}

@Composable
private fun ProfileItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}