package com.example.spaceflightnews

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spaceflightnews.adapters.ArticlesAdapter
import com.example.spaceflightnews.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerAdapter: ArticlesAdapter

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerAdapter = ArticlesAdapter {

        }

        binding.articlesRecyclerView.adapter = recyclerAdapter
        binding.articlesRecyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.articles.observe(this) {
            // TODO if != null itp
            recyclerAdapter.submitList(it.data)
            Log.v(TAG, it.data.toString())
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}