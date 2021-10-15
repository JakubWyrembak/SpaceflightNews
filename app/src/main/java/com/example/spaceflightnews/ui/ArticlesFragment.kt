package com.example.spaceflightnews.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spaceflightnews.*
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

    private var currentMode: ArticlesModes = ArticlesModes.MAIN

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticlesBinding.inflate(inflater)
        setHasOptionsMenu(true)

        getCurrentMode()
        setupRecycler()
        setupOtherViews()

        viewModel.articles.observe(viewLifecycleOwner) {
            checkCurrentViewState(it)
        }

        return binding.root
    }

    private fun setupRecycler() {
        recyclerAdapter = ArticlesAdapter { article ->
            navigateToDetail(article)
            if (requireActivity() is MainActivity) {
                UserData.addToHistory(article)      // TODO viewModel
                (activity as MainActivity).hideBottomNavigation()
            }
        }

        binding.articles.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupOtherViews() {
        with(binding){
            refreshLayout.setOnRefreshListener {
                viewModel.onRefresh()
            }
        }
    }

    private fun getCurrentMode() {
        val args: ArticlesFragmentArgs by navArgs()
        currentMode = when (args.articlesKey) {
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

    private fun checkCurrentViewState(stateMain: MainViewState) {
        when (stateMain) {
            is MainViewState.Success -> {
                binding.loadingProgressBar.visibility = View.GONE
                binding.refreshLayout.isRefreshing = false
                submitData(stateMain.data)
            }

            is MainViewState.Loading -> {
                binding.loadingProgressBar.visibility = View.VISIBLE
            }

            is MainViewState.Error -> {
                stateMain.message?.let { Log.e(TAG, it) }
            }
        }
    }

    private fun submitData(allArticlesData: List<Article>) {
        when (currentMode) {
            ArticlesModes.MAIN -> {
                recyclerAdapter.submitList(allArticlesData)
            }

            // TODO nie trzeba tego sprawdzać za każdym razem, może wystarczy tylko na początku
            ArticlesModes.HISTORY -> {
                recyclerAdapter.submitList(viewModel.getHistoryArticles())
            }

            ArticlesModes.FAVORITES -> {
                recyclerAdapter.submitList(viewModel.getFavoriteArticles())
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