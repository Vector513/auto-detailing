package com.example.routes

import com.example.models.Services
import com.example.models.toJsonString
import com.example.models.toServiceDto
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.serviceRoutes() {
    route("/services") {
        get {
            val queryParameters: Parameters = call.request.queryParameters
            val limitParam = queryParameters["limit"] ?: queryParameters["limits"]
            val limit = limitParam?.toIntOrNull()

            val services = transaction {
                val allServices = Services.selectAll().map { it.toServiceDto() }
                
                if (limit != null && limit > 0) {
                    allServices.take(limit)
                } else {
                    allServices
                }
            }
            
            call.respond(services)
        }

        post {
            val request = call.receive<CreateServiceRequest>()
            val newServiceId = transaction {
                Services.insert { stmt ->
                    stmt[Services.title] = request.title
                    stmt[Services.imageUrl] = request.imageUrl
                    stmt[Services.duration] = request.duration
                    stmt[Services.shortDesc] = request.shortDesc
                    stmt[Services.bulletPoints] = request.bulletPoints.toJsonString()
                    stmt[Services.priceFrom] = request.priceFrom
                }[Services.id].value
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to newServiceId))
        }
    }
}

@Serializable
data class CreateServiceRequest(
    val title: String,
    val imageUrl: String,
    val duration: String,
    val shortDesc: String,
    val bulletPoints: List<String>,
    val priceFrom: Int
)