// Автоматическое определение активной ссылки в навигации
document.addEventListener('DOMContentLoaded', function() {
  const pathname = window.location.pathname;
  const currentPage = pathname.split('/').pop() || 'index.html';
  const menuLinks = document.querySelectorAll('.header__menu-link');
  
  menuLinks.forEach(link => {
    // Убираем класс активной ссылки, если он уже есть
    link.classList.remove('header__menu-link--active');
    
    const linkHref = link.getAttribute('href');
    if (!linkHref) return;
    
    // Убираем якоря из href для сравнения
    const linkPage = linkHref.split('#')[0];
    
    // Проверяем соответствие текущей страницы
    if (linkPage === currentPage || 
        (currentPage === '' && linkPage === 'index.html') ||
        (currentPage === 'index.html' && linkPage === 'index.html') ||
        (pathname.endsWith('/') && linkPage === 'index.html')) {
      link.classList.add('header__menu-link--active');
    }
  });
});

