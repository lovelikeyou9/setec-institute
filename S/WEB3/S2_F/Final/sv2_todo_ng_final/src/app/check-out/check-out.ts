import { Component } from '@angular/core';
import { CartService } from '../service/cart-service';
import { CurrencyPipe } from '@angular/common';
import { DecimalPipe } from '@angular/common';



@Component({
  selector: 'app-check-out',
  imports: [DecimalPipe, CurrencyPipe],
  templateUrl: './check-out.html',
  styleUrl: './check-out.css'
})
export class CheckOut {
  constructor(public cartService: CartService) {

  }


}
