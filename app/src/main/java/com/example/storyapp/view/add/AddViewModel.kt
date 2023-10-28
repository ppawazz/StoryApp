package com.example.storyapp.view.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.repos.UserRepository
import com.example.storyapp.data.pref.UserModel
import java.io.File

class AddViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun uploadImage(token: String, file: File, description: String) =
        repository.uploadImage(token, file, description)
}