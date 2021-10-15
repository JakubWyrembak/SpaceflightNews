package com.example.spaceflightnews.utils

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.AppCompatImageButton
import com.example.spaceflightnews.R
import com.example.spaceflightnews.UserData
import com.example.spaceflightnews.model.Article

@SuppressLint("RestrictedApi")
fun View.favoriteButtonClicked(article: Article) {

    UserData.onFavoriteButtonClicked(article)
    val drawableIcon =
        AppCompatResources.getDrawable(
            context,
            if (article.isFavorite) R.drawable.ic_filled_heart else R.drawable.ic_favourite
        )!!

    when (this) {
        is AppCompatImageButton -> {
            Log.v("Extensions", "AppCompatImageView")
            setImageDrawable(drawableIcon)
        }

        is ActionMenuItemView -> {
            setIcon(drawableIcon)
        }
    }
}