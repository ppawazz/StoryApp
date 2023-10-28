package com.example.storyapp.view.signup

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.repos.UserRepository

class SignupViewModel(private val repository: UserRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) =
        repository.register(name, email, password)
}