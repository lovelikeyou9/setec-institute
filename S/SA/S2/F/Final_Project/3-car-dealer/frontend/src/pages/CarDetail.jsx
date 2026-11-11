import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';

export default function CarDetail() {
  const { id } = useParams();
  const [car, setCar] = useState(null);

  useEffect(() => {
    fetch(`http://localhost:5000/cars/${id}`)
      .then(res => res.json())
      .then(data => setCar(data));
  }, [id]);

  if (!car) return <p>Loading...</p>;

  return (
    <div className="car-detail">
      <img src={car.images[0]} alt={car.model} />
      <h2>{car.make} {car.model}</h2>
      <p>Year: {car.year}</p>
      <p>Mileage: {car.mileage} km</p>
      <p>Price: ${car.price}</p>
      <p>{car.description}</p>
      <Link to={`/checkout/${car._id}`} className="btn">Buy Now</Link>
    </div>
  );
}
