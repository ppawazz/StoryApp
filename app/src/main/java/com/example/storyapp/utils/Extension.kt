package com.example.storyapp.utils

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.showToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}