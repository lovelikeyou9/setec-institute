import { Injectable } from '@angular/core';

declare const axios: any;
declare const $: any;

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  constructor() {
    let vm = this;
    $.LoadingOverlay("show", {
      image: "https://media.tenor.com/I8ZX_XGKSrAAAAAj/blue-archive-alisu.gif", // your custom GIF URL
      imageAutoResize: true,  // optional: automatically scales
      imageAnimation: "2000ms rotate_right",  // optional: animation
      background: "rgba(21, 71, 129, 0.5)"  // optional: semi-transparent background
    });
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



  }

  private products: any = [];

  private url: string = 'https://sv-gen-api.bczin2zin2takeo.us/api/product';

  getProducts() {
    return this.products;
  }


}
