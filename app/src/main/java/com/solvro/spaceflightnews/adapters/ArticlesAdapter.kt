package com.solvro.spaceflightnews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.solvro.spaceflightnews.R
import com.solvro.spaceflightnews.databinding.SingleArticleBinding
import com.solvro.spaceflightnews.model.Article
import com.solvro.spaceflightnews.utils.changeFavoriteButtonIcon
import com.solvro.spaceflightnews.utils.makeGone
import com.solvro.spaceflightnews.utils.makeVisible


class ArticlesAdapter(
    private val onArticleClick: (Article) -> Unit,
    private val onFavoriteClick: (Article) -> Unit
) : ListAdapter<Article, ArticlesAdapter.ViewHolder>(
    ArticleDiffCallback
) {

    inner class ViewHolder(
        private val binding: SingleArticleBinding,
        private val showDetailedArticle: (Article) -> Unit,
        private val onFavoriteClick: (Article) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var currentArticle: Article

        fun bind(article: Article) {
            currentArticle = article
            setupTextViews()
            setupSummaryVisibility()
            loadImage()
            checkIfIsFavorite()
            setupListeners()
        }

        private fun setupTextViews() {
            with(binding) {
                title.text = currentArticle.title
                dateText.text = currentArticle.getPublishedTime()
                summary.text = currentArticle.summary
            }
        }

        private fun setupSummaryVisibility() {
            binding.summary.apply {
                if (currentArticle.summary.length >= MAX_SUMMARY_LENGTH) {
                    makeGone()
                } else {
                    makeVisible()
                }
            }
        }

        private fun loadImage() {
            Glide.with(binding.root)
                .load(currentArticle.imageUrl)
                .placeholder(R.drawable.ic_space_placeholder)
                .error(R.drawable.ic_error)
                .into(binding.image)
        }

        private fun checkIfIsFavorite() {
            binding.favoriteButton.setImageResource(
                if (currentArticle.isFavorite()) {
                    R.drawable.ic_filled_heart
                } else {
                    R.drawable.ic_favourite
                }
            )
        }

        private fun setupListeners() {
            with(binding) {
                articleCard.setOnClickListener {
                    showDetailedArticle(currentArticle)
                }

                favoriteButton.setOnClickListener {
                    it.changeFavoriteButtonIcon(currentArticle)
                    onFavoriteClick(currentArticle)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesAdapter.ViewHolder {
        val binding =
            SingleArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onArticleClick, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: ArticlesAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private const val MAX_SUMMARY_LENGTH = 128
        private const val TAG = "ArticlesAdapter"
    }
}

object ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldArticle: Article, newArticle: Article) =
        oldArticle.id == newArticle.id

    override fun areContentsTheSame(oldArticle: Article, newArticle: Article) =
        oldArticle == newArticle
}