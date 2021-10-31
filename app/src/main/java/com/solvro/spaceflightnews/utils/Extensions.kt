package com.solvro.spaceflightnews.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.solvro.spaceflightnews.R
import com.solvro.spaceflightnews.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        is AppCompatImageButton -> setImageDrawable(drawableIcon)
        is ActionMenuItemView -> setIcon(drawableIcon)
    }

    this.pulse()
}

fun View.pulse() {
    YoYo.with(Techniques.Pulse)
        .playOn(this)
}

fun View.makeGone() {
    this.visibility = View.GONE
}

fun View.makeVisible() {
    this.visibility = View.VISIBLE
}

fun <T> LifecycleOwner.observe(liveData: LiveData<T>, function: (T) -> Unit) {
    liveData.observe(this) {
        function(it)
    }
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

fun Fragment.launchIO(function: suspend ()-> Unit){
    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
        function()
    }
}



