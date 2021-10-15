package com.example.spaceflightnews.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.spaceflightnews.databinding.FragmentArticleWebsiteBinding

class ArticleWebsiteFragment : Fragment() {

    private var _binding: FragmentArticleWebsiteBinding? = null
    private val binding: FragmentArticleWebsiteBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleWebsiteBinding.inflate(inflater)

        val args: ArticleWebsiteFragmentArgs by navArgs()
        binding.webView.apply {
            loadUrl(args.websiteUrl)
            settings.javaScriptEnabled = true
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}