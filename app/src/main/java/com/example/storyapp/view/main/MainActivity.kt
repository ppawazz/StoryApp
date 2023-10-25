package com.example.storyapp.view.main

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.ResultState
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.utils.showToast
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.add.AddStoryActivity
import com.example.storyapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            fabCreate.contentDescription = getString(R.string.add_story)
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                binding.rvStory.layoutManager = LinearLayoutManager(this)
                setupView()
                setupAction(user.token)
                setupCreate()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu1 -> {
                viewModel.logout()
            }

            R.id.menu2 -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
        }
        return true
    }

    private fun setupAction(token: String) {
        viewModel.getStories(token).observe(this) { response ->
            with(binding) {
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
                        val adapter = ListStoryAdapter()
                        adapter.submitList(response.data)
                        rvStory.adapter = adapter
                    }
                }
            }
        }
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
    }

    private fun setupCreate() {
        binding.fabCreate.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }
}