// Load services for the dropdown
async function loadServices() {
    try {
        const response = await fetch('http://localhost:8080/api/services');
        if (!response.ok) {
            throw new Error('Failed to load services');
        }
        const services = await response.json();
        console.log('Загружены услуги для формы:', services);
        const serviceSelect = document.getElementById('service');

        if (!serviceSelect) {
            console.error('Элемент select с id="service" не найден');
            return;
        }

        // Clear existing options except the first one
        serviceSelect.innerHTML = '<option value="">ВЫБЕРИТЕ УСЛУГУ</option>';

        // Add services to dropdown
        if (services && services.length > 0) {
            services.forEach(service => {
                const option = document.createElement('option');
                option.value = service.title;
                option.textContent = service.title;
                serviceSelect.appendChild(option);
            });
            console.log(`Добавлено ${services.length} услуг в выпадающий список`);
        } else {
            console.warn('Список услуг пуст');
        }
    } catch (error) {
        console.error('Error loading services:', error);
    }
}

// Format phone number
function formatPhoneNumber(value) {
    // Remove all non-digit characters
    const digits = value.replace(/\D/g, '');

    // Format as +7(999)123-45-67
    if (digits.length === 0) return '';
    if (digits.length <= 1) return `+${digits}`;
    if (digits.length <= 4) return `+${digits[0]}(${digits.slice(1)}`;
    if (digits.length <= 7) return `+${digits[0]}(${digits.slice(1, 4)})${digits.slice(4)}`;
    if (digits.length <= 9) return `+${digits[0]}(${digits.slice(1, 4)})${digits.slice(4, 7)}-${digits.slice(7)}`;
    return `+${digits[0]}(${digits.slice(1, 4)})${digits.slice(4, 7)}-${digits.slice(7, 9)}-${digits.slice(9, 11)}`;
}

// Format date as dd.mm.yyyy
function formatDate(value) {
    const digits = value.replace(/\D/g, '');
    if (digits.length === 0) return '';
    if (digits.length <= 2) return digits;
    if (digits.length <= 4) return `${digits.slice(0, 2)}.${digits.slice(2)}`;
    return `${digits.slice(0, 2)}.${digits.slice(2, 4)}.${digits.slice(4, 8)}`;
}

// Format time as hh:mm
function formatTime(value) {
    const digits = value.replace(/\D/g, '');
    if (digits.length === 0) return '';
    if (digits.length <= 2) return digits;
    return `${digits.slice(0, 2)}:${digits.slice(2, 4)}`;
}

// Convert date from dd.mm.yyyy to yyyy-mm-dd
function convertDateToISO(dateString) {
    if (!dateString) return null;
    const parts = dateString.split('.');
    if (parts.length !== 3) return null;
    return `${parts[2]}-${parts[1]}-${parts[0]}`;
}

// Convert time from hh:mm to HH:mm:ss
function convertTimeToISO(timeString) {
    if (!timeString) return null;
    const parts = timeString.split(':');
    if (parts.length !== 2) return null;
    return `${parts[0].padStart(2, '0')}:${parts[1].padStart(2, '0')}:00`;
}

// Handle form submission
async function handleSubmit(event) {
    event.preventDefault();

    const form = event.target;
    const formData = new FormData(form);

    // Get form values
    const name = formData.get('name');
    const phone = formData.get('phone');
    const carModel = formData.get('carModel') || '';
    const service = formData.get('service');
    const date = formData.get('date');
    const time = formData.get('time');
    const additionalInfo = formData.get('additionalInfo') || '';

    // Validate required fields
    if (!name || !phone || !service) {
        alert('Пожалуйста, заполните все обязательные поля (отмечены *)');
        return;
    }

    // Prepare request data
    const bookingData = {
        name: name,
        phone: phone,
        carModel: carModel,
        serviceName: service,
        date: convertDateToISO(date) || new Date().toISOString().split('T')[0],
        time: convertTimeToISO(time) || '12:00:00',
        additionalInfo: additionalInfo || null
    };

    try {
        const response = await fetch('http://localhost:8080/api/bookings', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(bookingData)
        });

        if (!response.ok) {
            throw new Error('Failed to submit booking');
        }

        const result = await response.json();
        alert('Заявка успешно отправлена! Мы свяжемся с вами в течение часа.');
        form.reset();
    } catch (error) {
        console.error('Error submitting booking:', error);
        alert('Произошла ошибка при отправке заявки. Пожалуйста, попробуйте позже.');
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    loadServices();

    const form = document.getElementById('bookingForm');
    if (form) {
        form.addEventListener('submit', handleSubmit);
    }

    // Add input formatting
    const phoneInput = document.getElementById('phone');
    if (phoneInput) {
        phoneInput.addEventListener('input', (e) => {
            e.target.value = formatPhoneNumber(e.target.value);
        });
    }

    const dateInput = document.getElementById('date');
    if (dateInput) {
        dateInput.addEventListener('input', (e) => {
            e.target.value = formatDate(e.target.value);
        });
    }

    const timeInput = document.getElementById('time');
    if (timeInput) {
        timeInput.addEventListener('input', (e) => {
            e.target.value = formatTime(e.target.value);
        });
    }
});