package com.trevin.myplaylists.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevin.myplaylists.data.UserRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

sealed class AuthState {
    object Idle: AuthState()
    object Loading: AuthState()
    data class Success(val userID: Long): AuthState()
    data class Error(val message: String): AuthState()
}

class MainViewModel(private val userRepo: UserRepo) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    private val _signupState = MutableStateFlow<AuthState>(AuthState.Idle)
    val signupState: StateFlow<AuthState> = _signupState

    fun login(userName: String, password: String){
        if (userName.isBlank()){
            _loginState.value = AuthState.Error("User Name can't be empty")
            return
        } else if (password.isBlank()){
            _loginState.value = AuthState.Error("Password can't be empty")
            return
        }

        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            val user = userRepo.login(userName.trim(), password)
            _loginState.value = if (user != null){
                AuthState.Success(user.id)
            } else {
                AuthState.Error("Invalid Credentials")
            }
        }
    }

    fun signup(fullName: String, userName: String, password: String, confirmPass: String){
        when {
            fullName.isBlank() -> {
                _signupState.value = AuthState.Error("Full name is required")
                return
            }
            userName.isBlank() -> {
                _signupState.value = AuthState.Error("User name is required")
                return
            }
            password.length < 6 -> {
                _signupState.value = AuthState.Error("Password must be at least 6 characters")
                return
            }
            password.length > 16 -> {
                _signupState.value = AuthState.Error("Password must be less than 16 characters")
                return
            }
            password != confirmPass -> {
                _signupState.value = AuthState.Error("Passwords must match")
                return
            }
        }

        viewModelScope.launch {
            _signupState.value = AuthState.Loading
            try {
                val newID = userRepo.register(fullName, userName, password)
                _signupState.value = AuthState.Success(newID)
            } catch (e: Exception) {
                _signupState.value = AuthState.Error("User already exists!")
            }
        }
    }

    fun resetLoginState() { _loginState.value = AuthState.Idle }

    fun resetSignUpState(){ _signupState.value = AuthState.Idle }

    class Factory(private val userRepo: UserRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(userRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}