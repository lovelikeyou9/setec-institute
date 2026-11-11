import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  constructor() {
    this.cart = JSON.parse(localStorage.getItem('carts') ?? '[]');
  }

  private cart: any = [];

  addItem(item: any) {
    item.qty = 1;
    let dpl_index: number = this.cart.findIndex((obj: any) => obj.name == item.name);
    if (dpl_index > -1) {
      this.cart[dpl_index].qty += 1;
    }
    else {
      this.cart.push(item);
    }
    localStorage.setItem('carts', JSON.stringify(this.cart));

  }


  getItem(): any {
    return this.cart;
  }

  getTotal(): any {
    let total = 0;
    this.cart.forEach((item: { price: number; qty: number; }) => {
      total += item.price * item.qty;
    });
    return total;
  }

  removeItem(item: any){
    this.cart.splice(this.cart.indexOf(item), 1);
    localStorage.setItem('carts', JSON.stringify(this.cart));
  }

  incrementQty(item: any){
    let index = this.cart.indexOf(item);
    if(index > -1){
      this.cart[index].qty++;
      localStorage.setItem('carts', JSON.stringify(this.cart));
    }
  }

  decrementQty(item: any){
    let index = this.cart.indexOf(item);
    if(index > -1){
      this.cart[index].qty--;
      localStorage.setItem('carts', JSON.stringify(this.cart));
    }
  }


}


/*
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  constructor() {
    this.cart = JSON.parse(localStorage.getItem('carts') ?? '[]');
  }

  private cart: any = [];

  addItem(item: any) {
    let current_cart = JSON.parse(localStorage.getItem('carts') ?? '[]');
    let dpl_index: number = this.cart.findIndex((obj: any) => obj.id == item.id);
    if (dpl_index > -1) {
      current_cart[dpl_index].qty +=1;
      console.log(current_cart);
      localStorage.setItem('carts', JSON.stringify(current_cart));
    }
    else {
      item.qty = 1;
      this.cart.push(item);
      current_cart.push(item);
      localStorage.setItem('carts', JSON.stringify(current_cart));
    }
    

  }


  getItem(): any {
    return JSON.parse(localStorage.getItem('carts') ?? '[]');
  }

  getTotal(): any {
    let total = 0;
    this.cart.forEach((item: { price: number; qty: number; }) => {
      total += item.price * item.qty;
    });
    return total;
  }

  removeItem(item: any){
    this.cart.splice(this.cart.indexOf(item), 1);
    localStorage.setItem('carts', JSON.stringify(this.cart));
  }

  incrementQty(item: any){
    let index = this.cart.indexOf(item);
    if(index > -1){
      this.cart[index].qty++;
      localStorage.setItem('carts', JSON.stringify(this.cart));
    }
  }

  decrementQty(item: any){
    let index = this.cart.indexOf(item);
    if(index > -1){
      this.cart[index].qty--;
      localStorage.setItem('carts', JSON.stringify(this.cart));
    }
  }


}

*/