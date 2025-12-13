package com.example.routes

import com.example.models.Bookings
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalTime

fun Route.bookingRoutes() {
    route("/bookings") {
        post {
            val request = call.receive<CreateBookingRequest>()
            val newBookingId = transaction {
                Bookings.insert { stmt ->
                    stmt[Bookings.name] = request.name
                    stmt[Bookings.phone] = request.phone
                    stmt[Bookings.carModel] = request.carModel
                    stmt[Bookings.serviceName] = request.serviceName
                    stmt[Bookings.date] = LocalDate.parse(request.date)
                    stmt[Bookings.time] = LocalTime.parse(request.time)
                    stmt[Bookings.additionalInfo] = request.additionalInfo
                }[Bookings.id].value
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to newBookingId))
        }
    }
}

@Serializable
data class CreateBookingRequest(
    val name: String,
    val phone: String,
    val carModel: String,
    val serviceName: String,
    val date: String, // ISO date string (YYYY-MM-DD)
    val time: String, // ISO time string (HH:mm or HH:mm:ss)
    val additionalInfo: String? = null
)

