import { Component, EventEmitter,Input, Output } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-product-card',
  imports: [CurrencyPipe, RouterLink],
  templateUrl: './product-card.html',
  styleUrl: './product-card.css'
})
export class ProductCard {
  @Input() product: any = [];
  @Output() addToCart: any = new EventEmitter<any>();

  onAddToCart(product: any){
    return this.addToCart.emit(product);
  }

}
