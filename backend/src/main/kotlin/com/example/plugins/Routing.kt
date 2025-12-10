package com.example.plugins

import com.example.routes.healthRoutes
import com.example.routes.serviceRoutes
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        // Static assets served from resources/static
        staticResources("/static", "static")

        route("/api") {
            healthRoutes()
            serviceRoutes()
        }
    }
}

