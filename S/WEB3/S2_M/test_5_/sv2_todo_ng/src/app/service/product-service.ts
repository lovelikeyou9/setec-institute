import { Injectable } from '@angular/core';
declare const axios: any;
declare const $: any;

@Injectable({
  providedIn: 'root'
}) 

export class ProductService {

  constructor() {
    let vm = this;
    $.LoadingOverlay("show");
    axios.get(this.url)
      .then(function (response: any) {
        // handle success
        vm.products = response.data;
      })
      .catch(function (error: any) {
        // handle error
        console.log(error);
      }).finally(function(){
        $.LoadingOverlay("hide");
      })



    /*
    this.products = [
      {
        "id": 1,
        "name": "Fjallraven - Foldsack No. 1 Backpack, Fits 15 Laptops",
        "price": 109.95,
        "category": "men's clothing",
        "image": "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
        "qty": 247
      },
      {
        "id": 2,
        "name": "Mens Casual Premium Slim Fit T-Shirts ",
        "price": 22.3,
        "category": "men's clothing",
        "image": "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg",
        "qty": 396
      },
      {
        "id": 3,
        "name": "Mens Cotton Jacket",
        "price": 55.99,
        "category": "men's clothing",
        "image": "https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_.jpg",
        "qty": 385
      },
      {
        "id": 4,
        "name": "Mens Casual Slim Fit",
        "price": 15.99,
        "category": "men's clothing",
        "image": "https://fakestoreapi.com/img/71YXzeOuslL._AC_UY879_.jpg",
        "qty": 275
      },
      {
        "id": 5,
        "name": "John Hardy Women's Legends Naga Gold & Silver Dragon Station Chain Bracelet",
        "price": 695,
        "category": "jewelery",
        "image": "https://fakestoreapi.com/img/71pWzhdJNwL._AC_UL640_QL65_ML3_.jpg",
        "qty": 182
      },
      {
        "id": 6,
        "name": "Solid Gold Petite Micropave ",
        "price": 168,
        "category": "jewelery",
        "image": "https://fakestoreapi.com/img/61sbMiUnoGL._AC_UL640_QL65_ML3_.jpg",
        "qty": 453
      },
      {
        "id": 7,
        "name": "White Gold Plated Princess",
        "price": 9.99,
        "category": "jewelery",
        "image": "https://fakestoreapi.com/img/71YAIFU48IL._AC_UL640_QL65_ML3_.jpg",
        "qty": 490
      },
      {
        "id": 8,
        "name": "Pierced Owl Rose Gold Plated Stainless Steel Double",
        "price": 10.99,
        "category": "jewelery",
        "image": "https://fakestoreapi.com/img/51UDEzMJVpL._AC_UL640_QL65_ML3_.jpg",
        "qty": 102
      },
      {
        "id": 9,
        "name": "WD 2TB Elements Portable External Hard Drive - USB 3.0 ",
        "price": 64,
        "category": "electronics",
        "image": "https://fakestoreapi.com/img/61IBBVJvSDL._AC_SY879_.jpg",
        "qty": 412
      },
      {
        "id": 10,
        "name": "SanDisk SSD PLUS 1TB Internal SSD - SATA III 6 Gb/s",
        "price": 109,
        "category": "electronics",
        "image": "https://fakestoreapi.com/img/61U7T1koQqL._AC_SX679_.jpg",
        "qty": 489
      }
    ];
    */

  }

  private products: any = [];
  private url: string = 'https://sv-gen-api.bczin2zin2takeo.us/api/product_detail?id=1';





  getProducts() {
    return this.products;
  }

}
