package com.example.spaceflightnews.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.spaceflightnews.R
import com.example.spaceflightnews.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_articles, R.id.navigation_history, R.id.navigation_favorite
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    fun hideBottomNavigation(){
        binding.navView.clearAnimation()
        binding.navView.animate().translationY(
            binding.navView.height.toFloat()
        ).duration = BOTTOM_NAVIGATION_ANIMATION_DURATION
    }

    fun showBottomNavigation(){
        binding.navView.clearAnimation()
        binding.navView.animate().translationY(0F)
            .duration = BOTTOM_NAVIGATION_ANIMATION_DURATION
    }

    fun hideActionBar() {
        supportActionBar?.hide()
    }

    fun showActionBar() {
        supportActionBar?.show()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val BOTTOM_NAVIGATION_ANIMATION_DURATION = 300L
    }
}