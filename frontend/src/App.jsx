import React from "react";
import ServiceCard from "./ServiceCard";

function App() {
  const service = {
    id: 1,
    title: "Химчистка салона",
    image_url: "/static/img/sora.png",
    duration: "2-3 часа",
    short_desc: "Полная химчистка салона.",
    bullet_points: [
      "Чистка сидений",
      "Обработка потолка",
      "Очистка ковриков",
      "Устранение запахов"
    ],
    price_from: 3500
  };

  return (
    <div style={{ padding: "40px" }}>
      <ServiceCard service={service} />
    </div>
  );
}

export default App;
