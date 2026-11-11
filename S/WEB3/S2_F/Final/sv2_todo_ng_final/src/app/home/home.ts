import { Component } from '@angular/core';
import { MainSlider } from '../main-slider/main-slider';
import { ProductCard } from '../product-card/product-card';
import { CartService } from '../service/cart-service';
import { ProductService } from '../service/product-service';

declare const Swal: any;

@Component({
  selector: 'app-home',
  imports: [MainSlider, ProductCard],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {
  constructor(public cartService: CartService, public products: ProductService) {}

  onAddToCart(product: any) {
    Swal.fire({
      position: "top-end",
      icon: "success",
      title: "Added to Cart",
      showConfirmButton: false,
      timer: 1500
    });
    this.cartService.addItem(product);
  }
}
