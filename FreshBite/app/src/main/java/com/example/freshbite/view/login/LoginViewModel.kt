package com.example.freshbite.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshbite.data.Repository
import com.example.freshbite.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository) : ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun firebaseLogin(email: String, password: String) = repository.firebaseLogin(email, password)

}