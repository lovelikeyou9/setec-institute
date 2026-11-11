/*
import { Component } from '@angular/core';
import { CartService } from '../service/cart-service';

@Component({
  selector: 'app-product-detail',
  imports: [],
  templateUrl: './product-detail.html',
  styleUrl: './product-detail.css'
})
export class ProductDetail {
  constructor(public cartService: CartService){

  }



  
}
*/

import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CurrencyPipe } from '@angular/common';
import { CartService } from '../service/cart-service';

declare const axios: any;
declare const $: any;
declare const Swal: any;

@Component({
  selector: 'app-product-detail',
  imports: [CurrencyPipe],
  templateUrl: './product-detail.html',
  styleUrl: './product-detail.css'
})
export class ProductDetail {
  constructor(public route: ActivatedRoute, public cartService: CartService) {

  }
  public product: any = [];


  ngOnInit(): void {
    const product_id = this.route.snapshot.queryParamMap.get('product_id');
    //fetchAPI
    let vm = this;
    let url = 'https://sv-gen-api.bczin2zin2takeo.us/api/product_detail?id=' + product_id;
    $.LoadingOverlay("show", {
      image: "https://media.tenor.com/I8ZX_XGKSrAAAAAj/blue-archive-alisu.gif", // your custom GIF URL
      imageAutoResize: true,  // optional: automatically scales
      imageAnimation: "2000ms rotate_right",  // optional: animation
      background: "rgba(21, 71, 129, 0.5)"  // optional: semi-transparent background
    });
    axios.get(url)
      .then(function (response: any) {
        // handle success
        vm.product = response.data;
      })
      .catch(function (error: any) {
        // handle error
        console.log(error);
        vm.product = null;
      }).finally(function () {
        $.LoadingOverlay("hide");
      })

  }



  addToCart() {
    if (this.product) {
      this.cartService.addItem(this.product);
      Swal.fire({
        position: "top-end",
        icon: "success",
        title: "Added to Cart",
        showConfirmButton: false,
        timer: 1500
      });
    }
  }




}
