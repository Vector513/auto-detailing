async function loadServices() {
    try {
        // Определяем лимит в зависимости от страницы
        const currentPage = window.location.pathname.split('/').pop() || 'index.html';
        const limit = currentPage === 'services.html' ? null : 4;
        const url = limit ? `http://localhost:8080/api/services?limits=${limit}` : 'http://localhost:8080/api/services';

        const response = await fetch(url);
        if (!response.ok) {
            throw new Error('Ошибка загрузки услуг');
        }
        const services = await response.json();
        console.log('Загружены услуги:', services);
        displayServices(services);
    } catch (error) {
        console.error('Ошибка при загрузке услуг:', error);
        const servicesList = document.getElementById('servicesList');
        if (servicesList) {
            servicesList.innerHTML =
                '<li style="text-align: center; color: #fff; width: 100%;">Не удалось загрузить услуги</li>';
        }
    }
}

function displayServices(services) {
    const servicesList = document.getElementById('servicesList');
    if (!servicesList) return;

    servicesList.innerHTML = '';

    services.forEach((service, index) => {
        const serviceItem = document.createElement('li');
        serviceItem.className = `list__cards-item card__${index + 1}`;

        // Формируем полный URL для изображения
        const imageUrl = service.imageUrl.startsWith('http') ?
            service.imageUrl :
            `http://localhost:8080${service.imageUrl}`;

        // Форматируем цену
        const formattedPrice = new Intl.NumberFormat('ru-RU').format(service.priceFrom);

        // Создаем список bullet points
        const bulletPointsHtml = service.bulletPoints.map(point =>
            `<li class="services__list-item">
        <span class="services__list-text">${point}</span>
      </li>`
        ).join('');

        serviceItem.innerHTML = `
      <article class="card">
        <div class="services__header">
          <div class="services__icon-background">
            <img class="services__icon" src="${imageUrl}" alt="${service.title}" onerror="this.style.display='none'">
          </div>
          <h3 class="services__name">${service.title}</h3>
        </div>
        <div class="services__description">
          <p class="description">${service.shortDesc}</p>
        </div>
        <div class="services__body">
          <ul class="services__list">
            ${bulletPointsHtml}
          </ul>
        </div>
        <button class="btn-buy">
          <img src="/images/time_icon.png" class="logo__buy" onerror="this.style.display='none'">
          <div class="waiting__time">
            <span class="time">${service.duration}</span>
          </div>
          <div class="service__price">
            <span class="services__price-value">от ${formattedPrice} ₽</span>
          </div>
        </button>
      </article>
    `;

        servicesList.appendChild(serviceItem);
    });
}

// Загружаем услуги при загрузке страницы
document.addEventListener('DOMContentLoaded', loadServices);