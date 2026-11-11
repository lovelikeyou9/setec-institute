import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

export default function CarList() {
  const [cars, setCars] = useState([]);

  useEffect(() => {
    fetch(`http://localhost:5000/cars/${id}`)
      .then(res => res.json())
      .then(data => setCar(data));
  }, [id]);

  return (
    <div className="car-list">
      <h2>Available Cars</h2>
      <div className="grid">
        {cars.map(car => (
          <div key={car._id} className="card">
            <img src={car.images[0]} alt={car.model} />
            <h3>{car.make} {car.model}</h3>
            <p>${car.price}</p>
            <Link to={`/cars/${car._id}`}>View Details</Link>
          </div>
        ))}
      </div>
    </div>
  );
}
