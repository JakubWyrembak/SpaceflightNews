package com.example.spaceflightnews.ui.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spaceflightnews.states.ArticlesModes.*
import com.example.spaceflightnews.states.ArticlesModes
import com.example.spaceflightnews.ui.viewmodel.MainViewModel
import com.example.spaceflightnews.states.MainViewState
import com.example.spaceflightnews.states.MainViewState.*
import com.example.spaceflightnews.R
import com.example.spaceflightnews.adapters.ArticlesAdapter
import com.example.spaceflightnews.databinding.FragmentArticlesBinding
import com.example.spaceflightnews.model.Article
import com.example.spaceflightnews.utils.makeGone
import com.example.spaceflightnews.utils.makeVisible
import com.example.spaceflightnews.utils.showToast
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

        return binding.root
    }

    private fun setupCurrentMode() {
        val currentData =
            when (getCurrentMode()) {
                MAIN -> viewModel.articles
                HISTORY -> viewModel.historyArticles
                FAVORITES -> viewModel.favoriteArticles
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

        binding.articlesRecyclerView.apply {
            adapter = recyclerAdapter
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
        binding.refreshLayout.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.onRefresh(getCurrentMode())
            }
        }
    }

    private fun getCurrentMode(): ArticlesModes {
        val args: ArticlesFragmentArgs by navArgs()
        return when (args.articlesKey) {
            R.string.favorites_key -> FAVORITES
            R.string.history_key -> HISTORY
            else -> MAIN
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
            is Success -> onSuccess(state.data)
            is Loading -> onLoading()
            is Error -> {
                state.message?.let {
                    onError(it)
                }
            }
        }
    }

    private fun onError(errorMessage: String) {
        Log.e(TAG, errorMessage)
        showToast(errorMessage)
    }

    private fun onLoading() {
        if (!binding.refreshLayout.isRefreshing) {
            binding.loadingProgressBar.makeVisible()
        }
    }

    private fun onSuccess(articles: List<Article>) {
        with(binding) {
            loadingProgressBar.makeGone()
            refreshLayout.isRefreshing = false

            emptyListText.apply {
                if (articles.isEmpty()) {
                    makeVisible()
                } else {
                    makeGone()
                }
            }
        }

        recyclerAdapter.submitList(articles)
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