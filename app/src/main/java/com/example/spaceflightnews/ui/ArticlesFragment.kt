package com.example.spaceflightnews.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spaceflightnews.*
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
    private var currentMode: ArticlesModes = ArticlesModes.MAIN

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticlesBinding.inflate(inflater)
        setHasOptionsMenu(true)

        setupRecycler()
        setupOtherViews()

        setupCurrentMode()

        return binding.root
    }

    private fun setupCurrentMode() {
        when (getCurrentMode()) {
            ArticlesModes.MAIN -> {
                viewModel.articles.observe(viewLifecycleOwner) {
                    checkCurrentViewState(it)
                }
            }

            ArticlesModes.HISTORY -> {
                viewModel.historyArticles.observe(viewLifecycleOwner) {
                    recyclerAdapter.submitList(it)
                }
            }

            ArticlesModes.FAVORITES -> {
                // TODO lifecycle to nie w activity chyba sie powinno
                lifecycleScope.launch {
                    viewModel.loadFavorites()
                }
                viewModel.favoriteArticles.observe(viewLifecycleOwner) {
                    Log.e(TAG, "fav ${it.size} ${UserData.favorites}")
                    recyclerAdapter.submitList(it)
                }
            }
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
        }
    }

    private fun onFavoriteClick(id: Int) {
        lifecycleScope.launch {
            viewModel.addOrRemoveFavorite(id)
        }
    }

    private fun onArticleClick(article: Article) {
        lifecycleScope.launch {
            if (requireActivity() is MainActivity) {
                if (currentMode != ArticlesModes.HISTORY) {
                    viewModel.addToHistory(article.id)
                }
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
                binding.refreshLayout.isRefreshing = false
                recyclerAdapter.submitList(state.data)
            }

            is MainViewState.Loading -> {
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
            ArticlesFragmentDirections.actionNavigationToArticleDetailFragment(article)//actionNavigationArticlesMainToArticleDetailFragment(article)
        )

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        private const val TAG = "ArticlesFragment"
    }
}