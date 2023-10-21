package com.example.storyapp.view.detail

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.UserRepository

class DetailViewModel(private val repository: UserRepository) : ViewModel() {
    fun getDetail(id: String) = repository.getDetailStory(id)
}