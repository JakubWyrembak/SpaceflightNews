package com.example.spaceflightnews.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.spaceflightnews.R
import com.example.spaceflightnews.databinding.FragmentArticleDetailBinding
import com.example.spaceflightnews.model.Article

class ArticleDetailFragment : Fragment() {

    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleDetailBinding.inflate(inflater)

        val args: ArticleDetailFragmentArgs by navArgs()
        setupViews(args.articleDetail)
        return binding.root
    }

    private fun setupViews(article: Article) {
        with(binding) {
            Glide.with(root)
                .load(article.imageUrl)
                .placeholder(R.drawable.ic_space_placeholder)
                .into(image)

            title.text = article.title
            summary.text = article.summary
            updated.text = article.getUpdatedTime()
            site.text = article.site

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
}