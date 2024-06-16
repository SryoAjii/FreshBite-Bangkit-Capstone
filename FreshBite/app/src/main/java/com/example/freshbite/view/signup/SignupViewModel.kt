package com.example.freshbite.view.signup

import androidx.lifecycle.ViewModel
import com.example.freshbite.data.Repository

class SignupViewModel(private val repository: Repository) : ViewModel() {

    fun userRegister(username: String, email: String, password: String) = repository.userRegister(username, email, password)
}