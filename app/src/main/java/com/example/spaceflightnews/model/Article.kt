package com.example.spaceflightnews.model

import android.os.Parcelable
import com.example.spaceflightnews.states.UserData
import com.example.spaceflightnews.utils.reformatDate
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
    fun getPublishedTime() = published.reformatDate()

    fun getUpdatedTime() = updated.reformatDate()

    fun isFavorite() = this.id in UserData.favorites
}
