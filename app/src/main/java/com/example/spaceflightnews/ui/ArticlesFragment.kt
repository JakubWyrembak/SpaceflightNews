package com.example.spaceflightnews.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spaceflightnews.MainViewModel
import com.example.spaceflightnews.MainViewState
import com.example.spaceflightnews.R
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
        setHasOptionsMenu(true)

        recyclerAdapter = ArticlesAdapter {
            navigateToDetail(it)
            if (requireActivity() is MainActivity) {
                (activity as MainActivity).hideBottomNavigation()
            }
        }

        binding.articles.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.articles.observe(viewLifecycleOwner) {
            checkCurrentViewState(it)
        }

        binding.refreshLayout.setOnRefreshListener {
            viewModel.onRefresh()
        }

        return binding.root
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
                recyclerAdapter.submitList(stateMain.data)
            }

            is MainViewState.Loading -> {
                binding.loadingProgressBar.visibility = View.VISIBLE
            }

            is MainViewState.Error -> {
                stateMain.message?.let { Log.e(TAG, it) }
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