package com.example.spaceflightnews.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spaceflightnews.ArticlesModes
import com.example.spaceflightnews.MainViewModel
import com.example.spaceflightnews.MainViewState
import com.example.spaceflightnews.R
import com.example.spaceflightnews.adapters.ArticlesAdapter
import com.example.spaceflightnews.databinding.FragmentArticlesBinding
import com.example.spaceflightnews.model.Article
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ArticlesFragment : Fragment() {

    private var _binding: FragmentArticlesBinding? = null
    private val binding: FragmentArticlesBinding
        get() = _binding!!

    private lateinit var recyclerAdapter: ArticlesAdapter
    private val viewModel: MainViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticlesBinding.inflate(inflater)
        setHasOptionsMenu(true)

        setupRecycler()
        setupOtherViews()

        setupCurrentMode()

        Log.v(TAG, "OnCreateView")

        return binding.root
    }

    private fun setupCurrentMode() {
        val currentData =
            when (getCurrentMode()) {
                ArticlesModes.MAIN -> viewModel.articles
                ArticlesModes.HISTORY -> viewModel.historyArticles
                ArticlesModes.FAVORITES -> viewModel.favoriteArticles
            }

        currentData.observe(viewLifecycleOwner) {
            checkCurrentViewState(it)
        }
    }

    private fun setupRecycler() {
        recyclerAdapter = ArticlesAdapter(
            onFavoriteClick = {
                onFavoriteClick(it.id)
            },
            onArticleClick = { article: Article ->
                onArticleClick(article)
            })

        binding.articles.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(requireContext())
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun onFavoriteClick(id: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addOrRemoveFavorite(id)
        }
    }

    private fun onArticleClick(article: Article) {
        viewLifecycleOwner.lifecycleScope.launch {
            if (requireActivity() is MainActivity) {
                (activity as MainActivity).hideBottomNavigation()
            }
        }
        navigateToDetail(article)
    }

    private fun setupOtherViews() {
        with(binding) {
            refreshLayout.setOnRefreshListener {
                viewModel.loadData()
            }
        }
    }

    private fun getCurrentMode(): ArticlesModes {
        val args: ArticlesFragmentArgs by navArgs()
        return when (args.articlesKey) {
            R.string.favorites_key -> ArticlesModes.FAVORITES
            R.string.history_key -> ArticlesModes.HISTORY
            else -> ArticlesModes.MAIN
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.articles_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter_button -> {
                // TODO
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkCurrentViewState(state: MainViewState) {
        when (state) {
            is MainViewState.Success -> {
                binding.loadingProgressBar.visibility = View.GONE
                binding.articles.visibility = View.VISIBLE
                binding.refreshLayout.isRefreshing = false
                recyclerAdapter.submitList(state.data)
            }

            is MainViewState.Loading -> {
                binding.articles.visibility = View.GONE
                binding.loadingProgressBar.visibility = View.VISIBLE
            }

            is MainViewState.Error -> {
                state.message?.let { Log.e(TAG, it) }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity).showBottomNavigation()
        }
    }

    private fun navigateToDetail(article: Article) =
        findNavController().navigate(
            ArticlesFragmentDirections.actionNavigationToArticleDetailFragment(article)
        )

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        private const val TAG = "ArticlesFragment"
    }
}