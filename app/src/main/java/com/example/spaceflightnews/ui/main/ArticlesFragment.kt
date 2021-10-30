package com.example.spaceflightnews.ui.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceflightnews.R
import com.example.spaceflightnews.adapters.ArticlesAdapter
import com.example.spaceflightnews.databinding.FragmentArticlesBinding
import com.example.spaceflightnews.model.Article
import com.example.spaceflightnews.states.ArticlesMode
import com.example.spaceflightnews.states.ArticlesMode.*
import com.example.spaceflightnews.states.MainViewState
import com.example.spaceflightnews.states.MainViewState.*
import com.example.spaceflightnews.ui.viewmodel.MainViewModel
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

    private var searchView: SearchView? = null
    private lateinit var searchViewMenuItem: MenuItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticlesBinding.inflate(inflater)
        setHasOptionsMenu(true)

        setupRecycler()
        setupRefreshLayout()
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
            addOnScrollListener(getOnScrollListener())
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

    private fun setupRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener {
            searchViewMenuItem.collapseActionView()

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.onRefresh(getCurrentMode())
            }
        }
    }

    private fun getCurrentMode(): ArticlesMode {
        val args: ArticlesFragmentArgs by navArgs()
        return when (args.articlesKey) {
            R.string.favorites_key -> FAVORITES
            R.string.history_key -> HISTORY
            else -> MAIN
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.articles_menu, menu)

        searchViewMenuItem = menu.findItem(R.id.search_button)
        searchView = searchViewMenuItem.actionView as SearchView

        searchView!!.setOnQueryTextListener(getOnQueryTextListener())
        menu.findItem(R.id.search_button).setOnActionExpandListener(getOnActionExpandListener())

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter_button -> {
                // TODO
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkCurrentViewState(state: MainViewState) {
        when (state) {
            is Success -> onSuccess(state.data)
            is Loading -> onLoading()
            is Error -> onError(state.message)
        }
    }

    private fun onError(errorMessage: String?) {
        errorMessage?.let {
            Log.e(TAG, errorMessage)
            showToast(errorMessage)
        }
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

    private fun getOnScrollListener() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val last =
                (binding.articlesRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            if (shouldLoadMoreArticles(last)) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.fetchArticles()
                }
            }
        }
    }

    private fun shouldLoadMoreArticles(lastVisibleIndex: Int) =
        recyclerAdapter.itemCount - 1 == lastVisibleIndex
                && searchView?.query?.isEmpty() == true
                && viewModel.articles.value !is Loading
                && getCurrentMode() == MAIN

    private fun getOnQueryTextListener() = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            searchView!!.clearFocus()
            return true
        }

        override fun onQueryTextChange(query: String?): Boolean {
            if (!query.isNullOrEmpty()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    recyclerAdapter.submitList(
                        viewModel.getSearchedArticles(query, getCurrentMode())
                    )
                }
            }
            return true
        }
    }

    private fun getOnActionExpandListener() = object : MenuItem.OnActionExpandListener {
        override fun onMenuItemActionExpand(item: MenuItem?) = true

        override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.onRefresh(getCurrentMode())
            }
            return true
        }
    }

    companion object {
        private const val TAG = "ArticlesFragment"
    }
}