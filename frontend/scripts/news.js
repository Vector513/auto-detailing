async function loadNews() {
  try {
    const response = await fetch('http://localhost:8080/api/news?limits=3');
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
    const shortDescription = item.description && item.description.length > 0 
      ? item.description[0] 
      : '';

    // Формируем полный URL для изображения
    const imageUrl = item.imageUrl.startsWith('http') 
      ? item.imageUrl 
      : `http://localhost:8080${item.imageUrl}`;

    const img = document.createElement('img');
    img.src = imageUrl;
    img.alt = item.title;
    img.className = 'news__image';
    
    // Обработка ошибок загрузки изображения
    img.onerror = function() {
      console.error('Ошибка загрузки изображения:', imageUrl);
      this.style.display = 'none';
    };

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

