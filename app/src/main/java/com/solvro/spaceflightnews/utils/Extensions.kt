package com.solvro.spaceflightnews.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import com.solvro.spaceflightnews.R
import com.solvro.spaceflightnews.model.Article

@SuppressLint("RestrictedApi")
fun View.changeFavoriteButtonIcon(article: Article) {

    val drawableIcon =
        AppCompatResources.getDrawable(
            context,
            if (article.isFavorite()) {
                R.drawable.ic_favourite
            } else {
                R.drawable.ic_filled_heart
            }
        )!!

    when (this) {
        is AppCompatImageButton -> {
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
            it.toIntOrNull()        // TODO toInt?
        }

fun View.makeGone() {
    this.visibility = View.GONE
}

fun View.makeVisible() {
    this.visibility = View.VISIBLE
}

fun String.reformatDate() =
    (if (this.length >= DATE_END_INDEX)
        this.substring(0, DATE_END_INDEX)
    else
        this).replace("-", "/")


fun Activity.showToast(message: String) {
    Toast.makeText(
        this,
        message,
        Toast.LENGTH_SHORT
    ).show()
}

fun Fragment.showToast(message: String) = this.requireActivity().showToast(message)



