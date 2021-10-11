package com.example.spaceflightnews.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceflightnews.databinding.SingleArticleBinding
import com.example.spaceflightnews.model.Article

class ArticlesAdapter (private val onClick: (Article) -> Unit) :
ListAdapter<Article, ArticlesAdapter.ViewHolder>(DiffCallback) {

    // TODO kolejność
    inner class ViewHolder(
        private val binding: SingleArticleBinding,
        private val onClick: (Article) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root){

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
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