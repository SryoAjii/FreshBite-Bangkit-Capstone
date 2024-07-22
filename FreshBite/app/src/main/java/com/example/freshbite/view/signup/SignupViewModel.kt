package com.example.freshbite.view.signup

import androidx.lifecycle.ViewModel
import com.example.freshbite.data.Repository

class SignupViewModel(private val repository: Repository) : ViewModel() {

    fun firebaseRegister(username: String, email: String, password: String) = repository.firebaseRegister(username, email, password)
}