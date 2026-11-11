import { Component } from '@angular/core';
import { ProductCard } from '../product-card/product-card';
import { CartService } from '../service/cart-service';
import { ProductService } from '../service/product-service';


@Component({
  selector: 'app-product',
  imports: [ ProductCard ],
  templateUrl: './product.html',
  styleUrl: './product.css'
})


export class Product {

  constructor( public products: ProductService ) {

  }



}
