import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JsonPipe} from '@angular/common';
import { CurrencyPipe} from '@angular/common';

declare const axios: any;
declare const $: any;

@Component({
  selector: 'app-product-detail',
  imports: [ JsonPipe, CurrencyPipe ],
  templateUrl: './product-detail.html',
  styleUrl: './product-detail.css'
})
export class ProductDetail {
  constructor(public route: ActivatedRoute) {

  }

  public product: any = [];

  ngOnInit(): void {
    const product_id: string | null = this.route.snapshot.queryParamMap.get('product_id');
    //fetchAPI
    let vm = this;
    let url = 'https://sv-gen-api.bczin2zin2takeo.us/api/product_detail?id='+product_id;
    $.LoadingOverlay("show");
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

}
