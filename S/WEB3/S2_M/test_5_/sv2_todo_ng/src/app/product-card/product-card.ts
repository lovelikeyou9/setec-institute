import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { KhmerCurrencyPipe } from '../khmer-currency-in-pipe';
import { CurrencyPipe} from '@angular/common';
import { JsonPipe} from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-product-card',
  imports: [ CommonModule, KhmerCurrencyPipe, CurrencyPipe, JsonPipe, RouterModule  ],
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
