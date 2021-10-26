package com.example.spaceflightnews.model

import android.os.Parcelable
import com.example.spaceflightnews.UserData
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
    @Json(name = "publishedAt")
    val published: String,
) : Parcelable {
    fun getPublishedTime() = reformatDate(published)

    fun getUpdatedTime() = reformatDate(updated)

    private fun reformatDate(date: String) =
        if (date.length >= DATE_END_INDEX) date.substring(0, DATE_END_INDEX)
        else date

    fun isFavorite() = this.id in UserData.favorites
}
