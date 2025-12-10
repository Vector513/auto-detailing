import React, { useEffect, useState } from "react";
import ServiceCard from "./ServiceCard";

const API_BASE = import.meta.env.VITE_API_BASE ?? "http://localhost:8080/api";

function App() {
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const load = async () => {
      try {
        const res = await fetch(`${API_BASE}/services`);
        if (!res.ok) {
          throw new Error(`API error ${res.status}`);
        }
        const data = await res.json();
        // Backend возвращает camelCase, но подстрахуемся под оба варианта
        const normalized = data.map((item) => ({
          id: item.id,
          title: item.title ?? item.name ?? "",
          imageUrl: item.imageUrl ?? item.image_url ?? "/static/img/sora.png",
          duration: item.duration ?? "",
          shortDesc: item.shortDesc ?? item.short_desc ?? "",
          bulletPoints: item.bulletPoints ?? item.bullet_points ?? [],
          priceFrom: item.priceFrom ?? item.price_from ?? 0,
        }));
        setServices(normalized);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  if (loading) return <div style={{ padding: "24px" }}>Загрузка...</div>;
  if (error) return <div style={{ padding: "24px", color: "red" }}>{error}</div>;

  return (
    <div style={styles.grid}>
      {services.map((service) => (
        <ServiceCard key={service.id} service={service} />
      ))}
    </div>
  );
}

const styles = {
  grid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fill, minmax(300px, 1fr))",
    gap: "24px",
    padding: "40px",
    background: "#f5f7fa",
    minHeight: "100vh",
  },
};

export default App;
