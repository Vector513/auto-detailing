package com.example.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object Services : IntIdTable("services") {
    val title = varchar("title", length = 150)
    val imageUrl = varchar("image_url", length = 255)
    val duration = varchar("duration", length = 64)
    val shortDesc = varchar("short_desc", length = 512)
    // Store bullet points as JSON in a text column
    val bulletPoints = text("bullet_points")
    val priceFrom = integer("price_from")
}

@Serializable
data class ServiceDto(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val duration: String,
    val shortDesc: String,
    val bulletPoints: List<String>,
    val priceFrom: Int
)

fun ResultRow.toServiceDto(): ServiceDto = ServiceDto(
    id = this[Services.id].value,
    title = this[Services.title],
    imageUrl = this[Services.imageUrl],
    duration = this[Services.duration],
    shortDesc = this[Services.shortDesc],
    bulletPoints = runCatching {
        Json.decodeFromString<List<String>>(this[Services.bulletPoints])
    }.getOrDefault(emptyList()),
    priceFrom = this[Services.priceFrom]
)

fun List<String>.toJsonString(): String = Json.encodeToString(this)

