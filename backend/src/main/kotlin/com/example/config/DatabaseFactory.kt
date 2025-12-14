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
                title = "Детейлинг полный комплекс",
                imageUrl = "/static/img/polishing_icon.svg",
                duration = "1-2 дн.",
                shortDesc = "Полный комплекс услуг по восстановлению и защите автомобиля. " +
                        "Включает полировку, химчистку, керамику и защиту всех элементов.",
                bulletPoints = listOf(
                    "Полировка кузова (коррекция + финишная)",
                    "Химчистка салона с озонированием",
                    "Керамическое покрытие в 2 слоя",
                    "Защита стёкол и фар",
                    "Обработка дисков и хрома",
                    "Антикоррозийная обработка"
                ),
                priceFrom = 55000
            ),
            ServiceSeed(
                title = "Восстановление фар",
                imageUrl = "/static/img/protecting_icon.svg",
                duration = "3-5 ч.",
                shortDesc = "Полировка и восстановление помутневших фар. Возвращает прозрачность " +
                        "и светопропускание, защищает от дальнейшего помутнения.",
                bulletPoints = listOf(
                    "Снятие и разборка фар",
                    "Многоэтапная полировка (800-3000 grit)",
                    "Нанесение защитного покрытия",
                    "Установка и проверка герметичности",
                    "Гарантия на работу 1 год"
                ),
                priceFrom = 6000
            ),
            ServiceSeed(
                title = "Защита дисков",
                imageUrl = "/static/img/ceramic_сoating_icon.svg",
                duration = "4-6 ч.",
                shortDesc = "Керамическое покрытие литых и кованых дисков. Защищает от " +
                        "реагентов, тормозной пыли и сколов. Облегчает мойку.",
                bulletPoints = listOf(
                    "Снятие и мойка дисков",
                    "Обезжиривание и подготовка",
                    "Нанесение керамики в 2 слоя",
                    "Полимеризация в печи",
                    "Установка с защитой болтов"
                ),
                priceFrom = 8000
            ),
            ServiceSeed(
                title = "Химчистка двигателя",
                imageUrl = "/static/img/dry_cleaning_icon.svg",
                duration = "2-3 ч.",
                shortDesc = "Безопасная мойка подкапотного пространства. Удаление масляных " +
                        "пятен, грязи и нагара. Защита электроники и разъёмов.",
                bulletPoints = listOf(
                    "Защита электроники и разъёмов",
                    "Нанесение очищающих составов",
                    "Мойка паром и щётками",
                    "Сушка сжатым воздухом",
                    "Нанесение защитного покрытия"
                ),
                priceFrom = 5000
            ),
            ServiceSeed(
                title = "Антигравийная защита",
                imageUrl = "/static/img/protecting_icon.svg",
                duration = "6-8 ч.",
                shortDesc = "Оклейка уязвимых частей кузова защитной плёнкой. Защита от " +
                        "камней, сколов и царапин. Прозрачная или тонированная плёнка.",
                bulletPoints = listOf(
                    "Подготовка и обезжиривание поверхностей",
                    "Раскрой плёнки по лекалам",
                    "Оклейка капота, бампера, крыльев",
                    "Обработка краёв и стыков",
                    "Гарантия 5 лет"
                ),
                priceFrom = 25000
            ),
            ServiceSeed(
                title = "Озонирование салона",
                imageUrl = "/static/img/dry_cleaning_icon.svg",
                duration = "1-2 ч.",
                shortDesc = "Устранение запахов табака, животных, плесени и других. " +
                        "Озон проникает во все щели и уничтожает источник запаха.",
                bulletPoints = listOf(
                    "Предварительная химчистка",
                    "Герметизация салона",
                    "Генерация озона (30-60 мин)",
                    "Проветривание и контроль",
                    "Нанесение ароматизатора (опционально)"
                ),
                priceFrom = 3500
            ),
            ServiceSeed(
                title = "Полировка дисков",
                imageUrl = "/static/img/polishing_icon.svg",
                duration = "3-4 ч.",
                shortDesc = "Восстановление блеска литых и кованых дисков. Удаление царапин, " +
                        "окисления и потускнения. Возвращает заводской вид.",
                bulletPoints = listOf(
                    "Снятие дисков с автомобиля",
                    "Мойка и обезжиривание",
                    "Полировка вручную и машинная",
                    "Нанесение защитного покрытия",
                    "Установка обратно"
                ),
                priceFrom = 7000
            ),
            ServiceSeed(
                title = "Зимний комплекс защиты",
                imageUrl = "/static/img/ceramic_сoating_icon.svg",
                duration = "1 дн.",
                shortDesc = "Специальная подготовка автомобиля к зиме. Защита от реагентов, " +
                        "соли, низких температур и абразивного воздействия.",
                bulletPoints = listOf(
                    "Антикоррозийная обработка",
                    "Защита ЛКП от реагентов",
                    "Гидрофобное покрытие стёкол",
                    "Обработка замков и уплотнителей",
                    "Защита дисков и тормозов",
                    "Консервация кузова"
                ),
                priceFrom = 22000
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
                imageUrl = "/static/img/news1.webp",
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
                title = "ЭКО-ИНИЦИАТИВА: БЕЗОТХОДНЫЙ ДЕТЕЙЛИНГ",
                imageUrl = "/static/img/news2.svg",
                description = listOf(
                    "Наш сервис переходит на экологичные технологии! Мы стали первыми в отрасли, кто внедрил систему zero-waste."
                ),
                date = LocalDate.now().minusDays(3)
            ),
            NewsSeed(
                title = "ТЕХНОЛОГИЧЕСКОЕ ОБНОВЛЕНИЕ: CERAMIC PRO 10H",
                imageUrl = "/static/img/news3.svg",
                description = listOf(
                    "Мы первыми в России внедрили новое поколение керамических покрытий Ceramic Pro 10H с твердостью 10H по шкале Мооса!"
                ),
                date = LocalDate.now().minusDays(7)
            ),
            NewsSeed(
                title = "ОТКРЫТИЕ ВТОРОГО ЦЕНТРА В САНКТ-ПЕТЕРБУРГЕ",
                imageUrl = "/static/img/news4.svg",
                description = listOf(
                    "Рады сообщить об открытии нашего второго флагманского центра в Московском районе! Теперь мы ближе к вам."
                ),
                date = LocalDate.now().minusDays(2)
            ),
            NewsSeed(
                title = "НОВАЯ УСЛУГА: ЗИМНИЙ КОМПЛЕКС ЗАЩИТЫ",
                imageUrl = "/static/img/news5.svg",
                description = listOf(
                    "Мы запускаем специальный зимний комплекс защиты «ANTI-ICE»! Теперь ваш автомобиль готов к самым суровым условиям.",
                    "Что входит:",
                    "Нанесение зимнего антидора против реагентов и соли",
                    "Гидрофобное покрытие стёкол с эффектом -25°C",
                    "Обработка замков и уплотнителей от замерзания",
                    "Защита ЛКП от абразивного воздействия",
                    "Консервация кузова на зиму",
                    "Акция: При заказе комплекса — бесплатная химчистка ковриков.",
                    "«Зимняя защита увеличивает срок службы кузова на 30%», — говорит наш технолог Иван Петров."
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
