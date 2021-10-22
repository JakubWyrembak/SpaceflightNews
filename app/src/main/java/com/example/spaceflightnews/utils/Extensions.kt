package com.example.spaceflightnews.utils

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.AppCompatImageButton
import com.example.spaceflightnews.R
import com.example.spaceflightnews.model.Article

@SuppressLint("RestrictedApi")
fun View.changeFavoriteButtonIcon(article: Article) {

    val drawableIcon =
        AppCompatResources.getDrawable(
            context,
            if (article.isFavorite()) {
                Log.v("EXTENSIONS", "FILLED")
                R.drawable.ic_favourite
            } else {
                Log.v("EXTENSIONS", "EMPTY")
                R.drawable.ic_filled_heart
            }
        )!!

    when (this) {
        is AppCompatImageButton -> {
            Log.v("Extensions", "AppCompatImageButton")
            setImageDrawable(drawableIcon)
        }

        is ActionMenuItemView -> {
            setIcon(drawableIcon)
        }
    }
}

fun String.getArticlesIds(): List<Int> =
    removeSurrounding("[", "]")
        .split(", ")
        .mapNotNull {
            it.toIntOrNull()
        }




