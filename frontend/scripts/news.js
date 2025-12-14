async function loadNews() {
    try {
        // Определяем лимит в зависимости от страницы
        const currentPage = window.location.pathname.split('/').pop() || 'index.html';
        const limit = currentPage === 'news.html' ? null : 3;
        const url = limit ? `http://localhost:8080/api/news?limits=${limit}` : 'http://localhost:8080/api/news';

        const response = await fetch(url);
        if (!response.ok) {
            throw new Error('Ошибка загрузки новостей');
        }
        const news = await response.json();
        console.log('Загружены новости:', news);
        displayNews(news);
    } catch (error) {
        console.error('Ошибка при загрузке новостей:', error);
        const newsList = document.getElementById('newsList');
        if (newsList) {
            newsList.innerHTML =
                '<p style="text-align: center; color: #fff;">Не удалось загрузить новости</p>';
        }
    }
}

function displayNews(news) {
    const newsList = document.getElementById('newsList');
    if (!newsList) return;

    newsList.innerHTML = '';

    news.forEach(item => {
        const newsItem = document.createElement('div');
        newsItem.className = 'news__item';

        // Краткое описание - первый элемент из массива description
        const shortDescription = item.description && item.description.length > 0 ?
            item.description[0] :
            '';

        // Формируем полный URL для изображения
        const imageUrl = item.imageUrl.startsWith('http') ?
            item.imageUrl :
            `http://localhost:8080${item.imageUrl}`;

        const img = document.createElement('img');
        img.src = imageUrl;
        img.alt = item.title;
        img.className = 'news__image';

        // Обработка ошибок загрузки изображения - показываем placeholder
        img.onerror = function() {
            console.error('Ошибка загрузки изображения:', imageUrl);
            // Вместо скрытия, показываем placeholder
            this.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="209" height="170"%3E%3Crect width="209" height="170" fill="%232a2a2a"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23fff" font-size="14"%3EНет изображения%3C/text%3E%3C/svg%3E';
        };

        // Показываем картинку сразу
        img.style.display = 'block';

        const contentWrapper = document.createElement('div');
        contentWrapper.className = 'news__content-wrapper';

        const title = document.createElement('h3');
        title.className = 'news__item-title';
        title.textContent = item.title;

        const description = document.createElement('p');
        description.className = 'news__item-description';
        description.textContent = shortDescription;

        contentWrapper.appendChild(title);
        contentWrapper.appendChild(description);

        newsItem.appendChild(img);
        newsItem.appendChild(contentWrapper);

        newsList.appendChild(newsItem);
    });
}

// Загружаем новости при загрузке страницы
document.addEventListener('DOMContentLoaded', loadNews);