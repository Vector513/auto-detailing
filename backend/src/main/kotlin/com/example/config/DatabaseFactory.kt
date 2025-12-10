package com.example.config

import com.example.models.Services
import com.example.models.toJsonString
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect(hikari())
        transaction {
            if (autoMigrateEnabled()) {
                // Create tables on startup; replace with migrations for production
                SchemaUtils.createMissingTablesAndColumns(Services)
            }
            if (autoSeedEnabled()) {
                seedServicesIfEmpty()
            }
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = System.getenv("DATABASE_URL")
                ?: "jdbc:postgresql://localhost:5432/autodetailing"
            username = System.getenv("DATABASE_USER") ?: "autodetailer"
            // Default password set to your_password for local/manual DB
            password = System.getenv("DATABASE_PASSWORD") ?: "your_password"
            maximumPoolSize = 5
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }

    private fun autoMigrateEnabled(): Boolean =
        System.getenv("DB_AUTO_MIGRATE")?.lowercase() != "false"

    private fun autoSeedEnabled(): Boolean =
        System.getenv("DB_AUTO_SEED")?.lowercase() != "false"

    private fun seedServicesIfEmpty() {
        val isEmpty = Services.selectAll().empty()
        if (!isEmpty) return

        val seeds = listOf(
            ServiceSeed(
                title = "Химчистка салона",
                imageUrl = "/static/img/sora.png",
                duration = "2-3 часа",
                shortDesc = "Полная химчистка салона.",
                bulletPoints = listOf("Чистка сидений", "Обработка потолка", "Очистка ковриков", "Устранение запахов"),
                priceFrom = 3500
            ),
            ServiceSeed(
                title = "Полировка кузова",
                imageUrl = "/static/img/polish.png",
                duration = "4-6 часов",
                shortDesc = "Восстановление блеска и устранение мелких царапин.",
                bulletPoints = listOf("Мойка кузова", "Обезжиривание", "Полировка пастами", "Нанесение защитного состава"),
                priceFrom = 7000
            ),
            ServiceSeed(
                title = "Керамическое покрытие",
                imageUrl = "/static/img/ceramic.png",
                duration = "1 день",
                shortDesc = "Долговременная защита ЛКП.",
                bulletPoints = listOf("Подготовка поверхности", "Дегидрация", "Нанесение керамики", "Сушка под ИК-лампами"),
                priceFrom = 15000
            ),
            ServiceSeed(
                title = "Полная мойка и детейлинг интерьера",
                imageUrl = "/static/img/detailing.png",
                duration = "3-4 часа",
                shortDesc = "Комплексная мойка и уход за салоном.",
                bulletPoints = listOf("Бесконтактная мойка", "Пенный состав", "Уборка салона", "Кондиционирование кожи"),
                priceFrom = 5000
            ),
            ServiceSeed(
                title = "Антидождь стекол",
                imageUrl = "/static/img/rain.png",
                duration = "1 час",
                shortDesc = "Гидрофобное покрытие стекол.",
                bulletPoints = listOf("Очистка стекол", "Обезжиривание", "Нанесение покрытия", "Полировка"),
                priceFrom = 2500
            ),
            ServiceSeed(
                title = "Защита кузова воском",
                imageUrl = "/static/img/wax.png",
                duration = "2 часа",
                shortDesc = "Быстрая защита и глянец.",
                bulletPoints = listOf("Шампунь", "Обезжиривание", "Нанесение воска", "Финишная полировка"),
                priceFrom = 3000
            ),
            ServiceSeed(
                title = "Удаление битума и смолы",
                imageUrl = "/static/img/tar.png",
                duration = "1-2 часа",
                shortDesc = "Точечная очистка сложных загрязнений.",
                bulletPoints = listOf("Предварительная мойка", "Химическое удаление битума", "Контрольная мойка", "Нанесение защитного слоя"),
                priceFrom = 4000
            ),
            ServiceSeed(
                title = "Очистка и защита кожи",
                imageUrl = "/static/img/leather.png",
                duration = "2 часа",
                shortDesc = "Уход за кожаным салоном с защитой.",
                bulletPoints = listOf("Деликатная очистка", "Щётки/салфетки", "Кондиционирование", "Защитное покрытие"),
                priceFrom = 4500
            )
        )

        Services.batchInsert(seeds) { seed ->
            this[Services.title] = seed.title
            this[Services.imageUrl] = seed.imageUrl
            this[Services.duration] = seed.duration
            this[Services.shortDesc] = seed.shortDesc
            this[Services.bulletPoints] = seed.bulletPoints.toJsonString()
            this[Services.priceFrom] = seed.priceFrom
        }
    }

    private data class ServiceSeed(
        val title: String,
        val imageUrl: String,
        val duration: String,
        val shortDesc: String,
        val bulletPoints: List<String>,
        val priceFrom: Int
    )
}
