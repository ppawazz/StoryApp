package com.example.storyapp.view.add

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.example.storyapp.R
import com.example.storyapp.utils.ResultState
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.utils.getImageUri
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.showToast
import com.example.storyapp.utils.uriToFile
import com.example.storyapp.utils.ViewModelFactory
import com.example.storyapp.view.main.MainActivity

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel by viewModels<AddViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.apply {
            uploadImage.contentDescription = getString(R.string.add_image_description)
            btnCamera.contentDescription = getString(R.string.btn_add_camera)
            btnGallery.contentDescription = getString(R.string.add_gallery_description)
            textDescInput.contentDescription = getString(R.string.add_desc_description)
            buttonAdd.contentDescription = getString(R.string.btn_upload_description)
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, AddStoryActivity::class.java))
                finish()
            } else {
                setupView()
                playAnimation()
                binding.btnGallery.setOnClickListener {
                    startGallery()
                }
                binding.btnCamera.setOnClickListener {
                    startCamera()
                }
                uploadImage(user.token)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun uploadImage(token: String) {
        binding.buttonAdd.setOnClickListener {
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this).reduceFileImage()
                Log.d("Image File", "showImage: ${imageFile.path}")
                val description = binding.edAddDescription.text.toString()

                viewModel.uploadImage(token, imageFile, description).observe(this) { response ->
                    if (response != null) {
                        when (response) {
                            is ResultState.Loading -> {
                                binding.progressIndicator.isVisible = true
                            }

                            is ResultState.Success -> {
                                binding.progressIndicator.isVisible = false
                                showToast(response.data.message)
                                val toMain = Intent(this, MainActivity::class.java)
                                toMain.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(toMain)
                            }

                            is ResultState.Error -> {
                                binding.progressIndicator.isVisible = false
                                showToast(response.error)
                            }
                        }
                    }
                }
            } ?: showToast(getString(R.string.empty_image_warning))
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.uploadImage.setImageURI(it)
        }
    }

    private fun playAnimation() {
        val photo = ObjectAnimator.ofFloat(binding.uploadImage, View.ALPHA, 1f).setDuration(100)
        val description =
            ObjectAnimator.ofFloat(binding.edAddDescription, View.ALPHA, 1f).setDuration(100)
        val descEditTextLayout =
            ObjectAnimator.ofFloat(binding.textDescInput, View.ALPHA, 1f).setDuration(100)
        val camera = ObjectAnimator.ofFloat(binding.btnCamera, View.ALPHA, 1f).setDuration(100)
        val gallery = ObjectAnimator.ofFloat(binding.btnGallery, View.ALPHA, 1f).setDuration(100)
        val upload = ObjectAnimator.ofFloat(binding.buttonAdd, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playTogether(
                camera, gallery
            )
            playSequentially(
                photo, description, descEditTextLayout, upload
            )
            startDelay = 100
        }.start()
    }

    private fun setupView() {
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }
}