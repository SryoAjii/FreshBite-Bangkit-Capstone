package com.example.freshbite.view.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.freshbite.data.Repository
import com.example.freshbite.data.pref.UserModel
import kotlinx.coroutines.launch

class ProfileViewModel(private var repository: Repository): ViewModel(){

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun userLogout() = repository.userLogout()
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}