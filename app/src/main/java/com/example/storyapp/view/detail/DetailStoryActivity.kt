package com.example.storyapp.view.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.storyapp.data.ResultState
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.utils.showToast
import com.example.storyapp.view.ViewModelFactory

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, DetailStoryActivity::class.java))
                finish()
            } else {
                setupView()
                getDetail(user.token)
                playAnimation()
            }
        }
    }

    private fun getDetail(token: String) {
        val id = intent.getStringExtra(ID)
        with(binding) {
            id?.let {
                viewModel.getDetail(token, id).observe(this@DetailStoryActivity) { response ->
                    when (response) {
                        ResultState.Loading -> {
                            progressBar.isVisible = true
                        }

                        is ResultState.Error -> {
                            progressBar.isVisible = false
                            showToast(response.error)
                        }

                        is ResultState.Success -> {
                            progressBar.isVisible = false
                            val detailStory = response.data
                            tvDetailName.text = detailStory.name
                            tvDetailDesc.text = detailStory.description
                            Glide.with(binding.root)
                                .load(detailStory.photoUrl)
                                .into(ivDetailPhoto)
                        }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        val photo = ObjectAnimator.ofFloat(binding.ivDetailPhoto, View.ALPHA, 1f).setDuration(100)
        val name = ObjectAnimator.ofFloat(binding.tvDetailName, View.ALPHA, 1f).setDuration(100)
        val description =
            ObjectAnimator.ofFloat(binding.tvDetailDesc, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                photo,
                name,
                description,
            )
            startDelay = 100
        }.start()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    companion object {
        const val ID = "id_extra"
    }
}