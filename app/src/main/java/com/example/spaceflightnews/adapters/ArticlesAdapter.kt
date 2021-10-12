package com.example.spaceflightnews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.spaceflightnews.databinding.SingleArticleBinding
import com.example.spaceflightnews.model.Article

class ArticlesAdapter(private val onClick: (Article) -> Unit) :
    ListAdapter<Article, ArticlesAdapter.ViewHolder>(DiffCallback) {

    // TODO kolejność
    inner class ViewHolder(
        private val binding: SingleArticleBinding,
        private val onClick: (Article) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            with(binding){
                title.text = article.title

                if(article.summary.length >= 255){
                    description.visibility = View.GONE
                }else{
                    description.visibility = View.VISIBLE
                    description.text = article.summary
                }
                Glide.with(root)
                    .load(article.imageUrl)
                    .transform(RoundedCorners(36), FitCenter())
                    .into(image)
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
        private const val MAX_DESCRIPTION_LENGTH = 255
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