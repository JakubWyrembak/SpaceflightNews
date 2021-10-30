package com.solvro.spaceflightnews.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.solvro.spaceflightnews.R
import com.solvro.spaceflightnews.databinding.FragmentArticleWebsiteBinding

class ArticleWebsiteFragment : Fragment() {

    private var _binding: FragmentArticleWebsiteBinding? = null
    private val binding: FragmentArticleWebsiteBinding
        get() = _binding!!

    private lateinit var currentUrl: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleWebsiteBinding.inflate(inflater)

        setHasOptionsMenu(true)
        loadArgs()
        setupWebView()

        return binding.root
    }

    private fun setupWebView() {
        binding.webView.apply {
            loadUrl(currentUrl)
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()           // that fixes opening browser on NASA websites
        }
    }

    private fun loadArgs() {
        val args: ArticleWebsiteFragmentArgs by navArgs()
        currentUrl = args.websiteUrl
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.article_website_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share_button -> {
                val intentShare = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, currentUrl)
                }
                startActivity(Intent.createChooser(intentShare, "Share"))
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