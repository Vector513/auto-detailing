package com.example.routes

import com.example.models.News
import com.example.models.toNewsDto
import io.ktor.http.Parameters
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.newsRoutes() {
    route("/news") {
        get {
            val queryParameters: Parameters = call.request.queryParameters
            val limitParam = queryParameters["limit"] ?: queryParameters["limits"]
            val limit = limitParam?.toIntOrNull()

            val news = transaction {
                val allNews = News.selectAll()
                    .orderBy(News.id to SortOrder.DESC)
                    .map { it.toNewsDto() }
                
                if (limit != null && limit > 0) {
                    allNews.take(limit)
                } else {
                    allNews
                }
            }
            
            call.respond(news)
        }
        
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid news ID"))
                return@get
            }
            
            val news = transaction {
                News.selectAll()
                    .where { News.id eq id }
                    .map { it.toNewsDto() }
                    .firstOrNull()
            }
            
            if (news == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "News not found"))
            } else {
                call.respond(news)
            }
        }
    }
}

