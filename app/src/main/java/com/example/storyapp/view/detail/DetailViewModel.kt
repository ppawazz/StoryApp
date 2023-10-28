package com.example.storyapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.repos.UserRepository
import com.example.storyapp.data.pref.UserModel

class DetailViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getDetail(token: String, id: String) = repository.getDetailStory(token, id)
}