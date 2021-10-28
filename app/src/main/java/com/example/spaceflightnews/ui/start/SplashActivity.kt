package com.example.spaceflightnews.ui.start

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.spaceflightnews.ui.viewmodel.MainViewModel
import com.example.spaceflightnews.R
import com.example.spaceflightnews.databinding.ActivitySplashBinding
import com.example.spaceflightnews.ui.main.MainActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = getViewModel()
        hideBar()
        animate()

        lifecycleScope.launch{
            viewModel.loadPreferences()
        }
    }

    private fun hideBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
            )
        }
    }

    private fun animate() {
        val splashAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_splash)
        splashAnimation.setAnimationListener(getAnimationListener())

        binding.rocketIcon.animation = splashAnimation
    }

    private fun getAnimationListener() = object : Animation.AnimationListener {
        // TODO coroutines?
        override fun onAnimationEnd(animation: Animation?) {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }, 0)
        }

        override fun onAnimationStart(animation: Animation?) {}

        override fun onAnimationRepeat(animation: Animation?) {}
    }
}