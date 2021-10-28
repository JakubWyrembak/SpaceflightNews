package com.example.spaceflightnews.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.spaceflightnews.ui.viewmodel.MainViewModel
import com.example.spaceflightnews.R
import com.example.spaceflightnews.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch{
            viewModel.loadUserDataArticles()
        }

        setupNavigation()
    }

    private fun setupNavigation() {
        navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_articles, R.id.navigation_history, R.id.navigation_favorites
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    fun hideBottomNavigation() {
        binding.navView.apply {
            clearAnimation()
            animate().translationY(
                height.toFloat()
            ).duration = BOTTOM_NAVIGATION_ANIMATION_DURATION

            lifecycleScope.launchWhenCreated {
                delay(BOTTOM_NAVIGATION_ANIMATION_DURATION)
                visibility = View.GONE
            }
        }
    }

    fun showBottomNavigation() {
        binding.navView.apply{
            visibility = View.VISIBLE
            clearAnimation()
            animate().translationY(0F)
                .duration = BOTTOM_NAVIGATION_ANIMATION_DURATION

        }
    }

    override fun onPause() {
        viewModel.savePreferences()
        super.onPause()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val BOTTOM_NAVIGATION_ANIMATION_DURATION = 300L
    }
}