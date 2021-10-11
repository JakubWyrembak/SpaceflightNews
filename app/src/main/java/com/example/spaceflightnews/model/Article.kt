package com.example.spaceflightnews.model

import com.squareup.moshi.Json

data class Article(
    @Json(name="id")
    val id: Int,
    @Json(name="title")
    val title: String,
    @Json(name="url")
    val url: String,
    @Json(name="imageUrl")
    val imageUrl: String,
    @Json(name="newsSite")
    val site: String,
    @Json(name="summary")
    val summary: String,
    @Json(name="publishedAt")
    val published: String,
    @Json(name="updatedAt")
    val updated: String,

    val favorite: Boolean = false,
)
