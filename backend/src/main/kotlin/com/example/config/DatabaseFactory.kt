package com.example.config

import com.example.models.Bookings
import com.example.models.News
import com.example.models.Services
import com.example.models.toJsonString
import java.time.LocalDate
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
                // Create tables and add missing columns on startup
                // For production, use proper migration tools (e.g., Flyway)
                @Suppress("DEPRECATION")
                SchemaUtils.createMissingTablesAndColumns(Services, News, Bookings)
            }
            if (autoSeedEnabled()) {
                seedServicesIfEmpty()
                seedNewsIfEmpty()
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
                title = "Полировка кузова",
                imageUrl = "/static/img/polishing_icon.svg",
                duration = "8-12 ч.",
                shortDesc = "Восстановление лакокрасочного покрытия до состояния \"как из салона\". " +
                        "Удаление царапин, swirl-маркировки, окисления и мелких дефектов. Возвращает " +
                        "глубину цвета и зеркальный блеск.",
                bulletPoints = listOf(
                    "Мойка и обезжиривание",
                    "Машинная полировка (коррекция + финишная)",
                    "Нанесение защитного воска или синтетика",
                    "Полировка фар и хромовых элементов",
                    "Антистатическая обработка"
                ),
                priceFrom = 18000
            ),
            ServiceSeed(
                title = "Химчистка салона",
                imageUrl = "/static/img/dry_cleaning_icon.svg",
                duration = "6-10 ч.",
                shortDesc = "Глубокая очистка всех поверхностей салона с использованием профе" +
                        "ссионального оборудования. Удаление сложных загрязнений, запахов, " +
                        "восстановление цвета материалов.",
                bulletPoints = listOf(
                    "Чистка ковров и обивки экстрактором",
                    "Химчистка сидений, потолка, дверей",
                    "Очистка и кондиционирование кожи",
                    "Мойка панелей, пластика, декоративных вставок",
                    "Озонирование (устранение запахов)",
                    "Нанесение защиты на ткани и кожу"
                ),
                priceFrom = 12000
            ),
            ServiceSeed(
                title = "Керамическое покрытие",
                imageUrl = "/static/img/ceramic_сoating_icon.svg",
                duration = "2-3 дн.",
                shortDesc = "Нанесение многослойного керамического состава на кузов и диски. " +
                        "Создаёт эффект \"жидкого стекла\" с гидрофобными свойствами. Защищает" +
                        " от сколов, УФ-лучей, реагентов и мелких царапин. Автомобиль " +
                        "легко моется, дольше сохраняет блеск.",
                bulletPoints = listOf(
                    "Абразивная полировка кузова (3 этапа)",
                    "Обезжиривание и подготовка поверхностей",
                    "Нанесение керамики в 2 слоя с полимеризацией",
                    "Обработка стекол, фар, дисков",
                    "Гарантия 3 года"
                ),
                priceFrom = 35000
            ),
            ServiceSeed(
                title = "Защита кузова/стёкол",
                imageUrl = "/static/img/protecting_icon.svg",
                duration = "5-8 ч.",
                shortDesc = "Комплексная защита от внешних воздействий. Антидождь на стёкла, " +
                        "бронеплёнка на фары, антигравийная защита передней части.",
                bulletPoints = listOf(
                    "Нанесение гидрофобного покрытия на все стёкла",
                    "Защитная плёнка на фары (прозрачная или тонировка)",
                    "Обработка молдингов и резинок",
                    "Антикоррозийная обработка скрытых полостей",
                    "Защита пластика кузова"
                ),
                priceFrom = 9000
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

    private fun seedNewsIfEmpty() {
        // Check if table is empty - if so, populate with initial data
        val isEmpty = News.selectAll().empty()
        if (!isEmpty) return

        val seeds = listOf(
            NewsSeed(
                title = "Открытие нового филиала",
                imageUrl = "/static/img/news1.jpg",
                description = listOf(
                    "Рады сообщить об открытии нового филиала нашего авто-детейлинг центра в центре города",
                    "Новый филиал расположен в центре города",
                    "Современное оборудование от ведущих производителей",
                    "Опытные мастера с многолетним стажем",
                    "Удобная парковка для клиентов",
                    "Работаем ежедневно с 9:00 до 21:00"
                ),
                date = LocalDate.now().minusDays(5)
            ),
            NewsSeed(
                title = "Скидка 20% на керамическое покрытие",
                imageUrl = "/static/img/news2.jpg",
                description = listOf(
                    "Специальное предложение! До конца месяца скидка 20% на услугу керамического покрытия кузова",
                    "Скидка действует до конца месяца",
                    "Керамическое покрытие защищает кузов от сколов и царапин",
                    "Гидрофобный эффект сохраняется до 3 лет",
                    "Защита от УФ-излучения и реагентов",
                    "Применяем только сертифицированные материалы"
                ),
                date = LocalDate.now().minusDays(3)
            ),
            NewsSeed(
                title = "Новое оборудование для полировки",
                imageUrl = "/static/img/news3.jpg",
                description = listOf(
                    "Мы обновили парк оборудования и теперь используем новейшие полировальные машины от ведущих производителей",
                    "Полировальные машины Rupes BigFoot",
                    "Профессиональные пасты и полироли",
                    "Удаление царапин и swirl-маркеров",
                    "Восстановление глянца ЛКП",
                    "Гарантия качества работ"
                ),
                date = LocalDate.now().minusDays(7)
            ),
            NewsSeed(
                title = "Акция: Комплексная защита автомобиля",
                imageUrl = "/static/img/news4.jpg",
                description = listOf(
                    "При заказе полного комплекса услуг по защите кузова и салона - скидка 15%",
                    "Скидка 15% на комплекс услуг",
                    "Полировка кузова с удалением дефектов",
                    "Керамическое покрытие в 2 слоя",
                    "Химчистка салона с защитой",
                    "Гидрофобное покрытие всех стекол",
                    "Антидождь на лобовое стекло"
                ),
                date = LocalDate.now().minusDays(2)
            ),
            NewsSeed(
                title = "Мастер-класс по уходу за автомобилем",
                imageUrl = "/static/img/news5.jpg",
                description = listOf(
                    "Приглашаем всех желающих на бесплатный мастер-класс по правильному уходу за автомобилем",
                    "Бесплатное участие для всех желающих",
                    "Обучение правильной мойке автомобиля",
                    "Техники полировки вручную",
                    "Выбор средств для защиты кузова",
                    "Практические советы от профессионалов",
                    "Регистрация обязательна"
                ),
                date = LocalDate.now().plusDays(7)
            )
        )

        News.batchInsert(seeds) { seed ->
            this[News.title] = seed.title
            this[News.imageUrl] = seed.imageUrl
            this[News.description] = seed.description.toJsonString()
            this[News.date] = seed.date
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

    private data class NewsSeed(
        val title: String,
        val imageUrl: String,
        val description: List<String>,
        val date: LocalDate
    )
}
