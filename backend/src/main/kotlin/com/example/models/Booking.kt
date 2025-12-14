package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.time
import java.time.LocalDate
import java.time.LocalTime

object Bookings : IntIdTable("bookings") {
    val name = varchar("name", length = 100)
    val phone = varchar("phone", length = 20)
    val carModel = varchar("car_model", length = 100)
    val serviceName = varchar("service_name", length = 200)
    val date = date("date")
    val time = time("time")
    val additionalInfo = text("additional_info").nullable()
}

@Serializable
data class BookingDto(
    val id: Int,
    val name: String,
    val phone: String,
    val carModel: String,
    val serviceName: String,
    val date: String, // ISO date string
    val time: String, // ISO time string
    val additionalInfo: String?
)

fun ResultRow.toBookingDto(): BookingDto = BookingDto(
    id = this[Bookings.id].value,
    name = this[Bookings.name],
    phone = this[Bookings.phone],
    carModel = this[Bookings.carModel],
    serviceName = this[Bookings.serviceName],
    date = this[Bookings.date].toString(),
    time = this[Bookings.time].toString(),
    additionalInfo = this[Bookings.additionalInfo]
)

