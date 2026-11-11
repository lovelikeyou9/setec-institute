import { Component } from '@angular/core';
import { ProductCard } from '../product-card/product-card';
import { ProductService } from '../service/product-service';
import { CartService } from '../service/cart-service';

declare const Swal: any;

@Component({
  selector: 'app-product',
  imports: [ProductCard],
  templateUrl: './product.html',
  styleUrl: './product.css'
})
export class Product {
  constructor(public products: ProductService, public cartService: CartService) {}

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
