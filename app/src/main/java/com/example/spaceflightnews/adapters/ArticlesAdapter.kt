package com.example.spaceflightnews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.spaceflightnews.R
import com.example.spaceflightnews.UserData
import com.example.spaceflightnews.databinding.SingleArticleBinding
import com.example.spaceflightnews.model.Article
import com.example.spaceflightnews.utils.favoriteButtonClicked

class ArticlesAdapter(private val onClick: (Article) -> Unit) :
    ListAdapter<Article, ArticlesAdapter.ViewHolder>(DiffCallback) {

    // TODO kolejność
    inner class ViewHolder(
        private val binding: SingleArticleBinding,
        private val showDetailedArticle: (Article) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var currentArticle: Article

        fun bind(article: Article) {
            currentArticle = article
            setupTextViews()
            setupSummaryVisibility()
            loadImage()
            checkIfIsFavorite()
            setupListener()
        }

        private fun checkIfIsFavorite() {
            if(currentArticle.isFavorite){
                binding.favoriteButton.setImageResource(R.drawable.ic_filled_heart)
            }else{
                binding.favoriteButton.setImageResource(R.drawable.ic_favourite)
            }
        }

        private fun setupTextViews() {
            currentArticle.let { article ->
                with(binding) {
                    title.text = article.title
                    date.text = article.getUpdatedTime()
                    summary.text = article.summary
                }
            }
        }

        private fun setupSummaryVisibility() {
            binding.summary.visibility =
                if (currentArticle.summary.length >= MAX_SUMMARY_LENGTH) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
        }

        private fun loadImage() {
            Glide.with(binding.root)
                .load(currentArticle.imageUrl)
                .placeholder(R.drawable.ic_space_placeholder)
                .error(R.drawable.ic_error)
                .into(binding.image)
        }

        private fun setupListener() {
            with(binding) {
                articleCard.setOnClickListener { showDetailedArticle(currentArticle) }
                favoriteButton.setOnClickListener {
                    onFavoriteButtonClicked()
                }
            }
        }

        private fun onFavoriteButtonClicked() {
            with(binding) {
                favoriteButton.favoriteButtonClicked(currentArticle)

                YoYo.with(Techniques.Pulse)
                    .playOn(favoriteButton)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            SingleArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private const val MAX_SUMMARY_LENGTH = 128
        private const val TAG = "ArticlesAdapter"
    }
}


// TODO
// ogarnąć jakie atrybuty dawać w areContentsTheSame
// Nazwy dać lepsze
object DiffCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.id == newItem.id && oldItem.id == newItem.id
    }
}