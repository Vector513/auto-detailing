import React from "react";

export default function ServiceCard({ service }) {
  const bullets = service.bulletPoints ?? [];

  return (
    <div style={styles.card}>
      <img
        src={service.imageUrl}
        alt={service.title}
        style={styles.image}
      />

      <h2 style={styles.title}>{service.title}</h2>
      <p style={styles.duration}>{service.duration}</p>

      <p style={styles.desc}>{service.shortDesc}</p>

      {bullets.length > 0 && (
        <ul style={styles.list}>
          {bullets.map((item, idx) => (
            <li key={idx}>{item}</li>
          ))}
        </ul>
      )}

      <p style={styles.price}>от {service.priceFrom} ₽</p>
    </div>
  );
}

const styles = {
  card: {
    width: "300px",
    padding: "16px",
    borderRadius: "12px",
    boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
    background: "#fff",
    display: "flex",
    flexDirection: "column",
    gap: "8px",
  },
  image: {
    width: "100%",
    height: "160px",
    objectFit: "cover",
    borderRadius: "8px",
  },
  title: {
    margin: 0,
    color: "#222", // тёмный цвет для заголовка
  },
  duration: {
    fontSize: "14px",
    color: "#666",
    margin: 0,
  },
  desc: {
    fontSize: "14px",
    color: "#333", // тёмный цвет для текста описания
  },
  list: {
    paddingLeft: "20px",
    margin: 0,
    color: "#333", // тёмный цвет для пунктов списка
  },
  price: {
    marginTop: "10px",
    fontWeight: "bold",
    fontSize: "18px",
    color: "#000", // тёмный цвет для цены
  },
};
