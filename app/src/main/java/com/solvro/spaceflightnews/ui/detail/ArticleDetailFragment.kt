package com.solvro.spaceflightnews.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.solvro.spaceflightnews.R
import com.solvro.spaceflightnews.databinding.FragmentArticleDetailBinding
import com.solvro.spaceflightnews.model.Article
import com.solvro.spaceflightnews.ui.viewmodel.MainViewModel
import com.solvro.spaceflightnews.utils.changeFavoriteButtonIcon
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ArticleDetailFragment : Fragment() {

    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var article: Article

    private val viewModel: MainViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleDetailBinding.inflate(inflater)

        setHasOptionsMenu(true)

        val args: ArticleDetailFragmentArgs by navArgs()
        article = args.articleDetail

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addToHistory(article.id)
        }

        setupViews()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.article_detail_menu, menu)
        menu.findItem(R.id.favorite_button).setIcon(
            if (article.isFavorite()) {
                R.drawable.ic_filled_heart
            } else {
                R.drawable.ic_favourite
            }
        )

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorite_button -> {
                requireActivity().findViewById<View>(item.itemId).changeFavoriteButtonIcon(article)
                lifecycleScope.launch {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.addOrRemoveFavorite(article.id)
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViews() {
        with(binding) {
            Glide.with(root)
                .load(article.imageUrl)
                .placeholder(R.drawable.ic_space_placeholder)
                .into(image)

            title.text = article.title
            summary.text = article.summary
            publishedText.text = article.getPublishedTime()
            siteText.text = article.site
            lastUpdate.text = getString(R.string.updated, article.getUpdatedTime())
            websiteButton.setOnClickListener {
                navigateToWebsite(article.url)
            }
        }
    }

    private fun navigateToWebsite(url: String) {
        findNavController().navigate(
            ArticleDetailFragmentDirections.actionArticleDetailFragmentToArticleWebsiteFragment(url)
        )
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        private const val TAG = "ArticleDetailFragment"
    }
}