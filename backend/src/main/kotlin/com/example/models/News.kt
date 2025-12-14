package com.example.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object News : IntIdTable("news") {
    val title = varchar("title", length = 200)
    val imageUrl = varchar("image_url", length = 255)
    // Store description bullet points as JSON in a text column
    val description = text("description")
    val date = date("date")
}

@Serializable
data class NewsDto(
    val id: Int,
    val title: String,
    val description: List<String>,
    val date: String, // ISO date string
    val imageUrl: String
)

fun ResultRow.toNewsDto(): NewsDto = NewsDto(
    id = this[News.id].value,
    title = this[News.title],
    description = runCatching {
        Json.decodeFromString<List<String>>(this[News.description])
    }.getOrDefault(emptyList()),
    date = this[News.date].toString(),
    imageUrl = this[News.imageUrl]
)

