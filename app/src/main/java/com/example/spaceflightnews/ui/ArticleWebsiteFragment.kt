package com.example.spaceflightnews.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.spaceflightnews.R
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

        setHasOptionsMenu(true)
        val args: ArticleWebsiteFragmentArgs by navArgs()
        binding.webView.apply {
            loadUrl(args.websiteUrl)
            settings.javaScriptEnabled = true
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_article_website, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.share_button -> {
                // TODO
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}