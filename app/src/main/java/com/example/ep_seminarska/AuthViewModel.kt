package com.example.ep_seminarska

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class AuthState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    private val authService: AuthService // You'll need to inject this

    init {
        // Initialize your API service here
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        authService = retrofit.create(AuthService::class.java)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            try {
                Log.i("BEFORE REQ: ", "$email - $password")
                val response = authService.authenticate("api", "authenticate", email, password)
                Log.i("AFTER REQ: ", "$response")
                if (response.status == "success" && response.data.authenticated) {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        user = response.data.user,
                        error = null
                    )
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = "Authentication failed"
                    )
                }
            } catch (e: Exception) {
                Log.i("AUTH ERR: ", e.toString())
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun logout() {
        _authState.value = AuthState()
    }
}