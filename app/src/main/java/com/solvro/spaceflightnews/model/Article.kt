package com.solvro.spaceflightnews.model

import android.os.Parcelable
import com.solvro.spaceflightnews.states.UserData
import com.solvro.spaceflightnews.utils.reformatDate
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
    private val updatedTime: String,
    @Json(name = "publishedAt")
    private val publishedTime: String,
) : Parcelable {
    fun getPublishedTime() = publishedTime.reformatDate()

    fun getUpdatedTime() = updatedTime.reformatDate()

    fun isFavorite() = this.id in UserData.favorites
}
