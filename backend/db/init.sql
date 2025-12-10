-- Initial schema for services table matching ServiceDto
CREATE TABLE IF NOT EXISTS services (
    id SERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    duration VARCHAR(64) NOT NULL,
    short_desc VARCHAR(512) NOT NULL,
    bullet_points TEXT NOT NULL,
    price_from INTEGER NOT NULL
);

