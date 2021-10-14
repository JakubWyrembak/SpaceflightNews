package com.example.spaceflightnews.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spaceflightnews.MainActivity
import com.example.spaceflightnews.MainViewModel
import com.example.spaceflightnews.adapters.ArticlesAdapter
import com.example.spaceflightnews.databinding.FragmentArticlesBinding
import com.example.spaceflightnews.model.Article

class ArticlesFragment : Fragment() {

    private var _binding: FragmentArticlesBinding? = null
    private val binding: FragmentArticlesBinding
        get() = _binding!!

    private lateinit var recyclerAdapter: ArticlesAdapter
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticlesBinding.inflate(inflater)

        recyclerAdapter = ArticlesAdapter {
            navigateToDetail(it)
            if (requireActivity() is MainActivity) {
                (activity as MainActivity).hideBottomNavigation()
            }
        }

        binding.articles.adapter = recyclerAdapter
        binding.articles.layoutManager = LinearLayoutManager(requireContext())

        viewModel.articles.observe(viewLifecycleOwner) {
            // TODO if != null itp
            recyclerAdapter.submitList(it.data)
            Log.v(TAG, it.data.toString())
        }

        binding.refreshLayout.setOnRefreshListener {
            Log.v(TAG, "Odświeżam")
            recyclerAdapter.submitList(viewModel.articles.value?.data)
            binding.refreshLayout.isRefreshing = false
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity).showBottomNavigation()
        }
    }

    private fun navigateToDetail(article: Article) =
        findNavController().navigate(
            ArticlesFragmentDirections.actionNavigationArticlesToArticleDetailFragment(article)
        )

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        private const val TAG = "ArticlesFragment"
    }
}