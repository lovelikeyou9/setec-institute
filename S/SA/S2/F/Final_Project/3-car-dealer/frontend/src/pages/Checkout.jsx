import React, { useState } from 'react';
import { useParams } from 'react-router-dom';

export default function Checkout() {
  const { id } = useParams();
  const [status, setStatus] = useState('');

  const handleCheckout = async () => {
    const token = localStorage.getItem('token');
    const res = await fetch('http://localhost:5000/orders', {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ carId: id })
    });
    const data = await res.json();
    setStatus(data.message);
  };


  return (
    <div className="checkout">
      <h2>Checkout</h2>
      <button onClick={handleCheckout}>Confirm Purchase</button>
      {status && <p>{status}</p>}
    </div>
  );
}
