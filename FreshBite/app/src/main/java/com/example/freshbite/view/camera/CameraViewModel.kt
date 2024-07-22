package com.example.freshbite.view.camera

import androidx.lifecycle.ViewModel
import com.example.freshbite.data.Repository

class CameraViewModel (private var repository: Repository): ViewModel() {

    fun getUser() = repository.getUserDetail()
}