import React from 'react';
import { Link } from 'react-router-dom';

export default function Home() {
  return (
    <div>
      <header className="hero">
        <h1>Welcome to CarDealer</h1>
        <p>Find your dream car today</p>
        <Link to="/cars" className="btn">Browse Cars</Link>
      </header>
    </div>
  );
}
