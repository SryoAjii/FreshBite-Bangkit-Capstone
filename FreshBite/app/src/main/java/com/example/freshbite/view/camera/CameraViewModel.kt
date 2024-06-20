package com.example.freshbite.view.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.freshbite.data.Repository
import com.example.freshbite.data.pref.UserModel

class CameraViewModel (private var repository: Repository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}