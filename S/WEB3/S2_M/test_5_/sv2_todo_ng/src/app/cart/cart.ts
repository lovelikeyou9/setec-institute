import { Component } from '@angular/core';
import { CartService } from '../service/cart-service';
import { JsonPipe } from '@angular/common';
import { CurrencyPipe, DecimalPipe } from '@angular/common';
declare const Swal: any;

@Component({
  selector: 'app-cart',
  imports: [ JsonPipe, CurrencyPipe, DecimalPipe ],
  templateUrl: './cart.html',
  styleUrl: './cart.css'
})
export class Cart {
  constructor(public cartService: CartService) { }

  removeItem(item: any) {
    Swal.fire({
      title: "Are you sure?",
      text: "You won't be able to revert this!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Yes, remove it!"
    }).then((result: any) => {
      if (result.isConfirmed) {
        this.cartService.removeItem(item);
        Swal.fire({
          title: "Removed!",
          text: "Your cart Item has been deleted.",
          icon: "success"
        });
      }
    });

  }


  decrementQty(item: any) {
    if (item.qty > 1) {
      this.cartService.decrementQty(item);
    }
  }

  incrementQty(item: any) {
      this.cartService.incrementQty(item);
  }



}
