package com.example.spaceflightnews.model

import android.graphics.Bitmap
import android.os.Parcelable
import com.example.spaceflightnews.utils.DATE_END_INDEX
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    @Json(name = "id")
    val id: Int,
    @Json(name = "title")
    val title: String,
    @Json(name = "url")
    val url: String,
    @Json(name = "imageUrl")
    val imageUrl: String,
    @Json(name = "newsSite")
    val site: String,
    @Json(name = "summary")
    val summary: String,
    @Json(name = "updatedAt")
    val updated: String,

    // TODO change to val
    var isFavorite: Boolean = false,
) : Parcelable {
    fun getUpdatedTime() =
        if (updated.length >= DATE_END_INDEX) updated.substring(0, DATE_END_INDEX)
        else updated
}
