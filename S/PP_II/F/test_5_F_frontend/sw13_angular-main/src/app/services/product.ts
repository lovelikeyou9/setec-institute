import { Injectable } from '@angular/core';
declare const axios: any;
declare const $: any;

@Injectable({
  providedIn: 'root',
})
export class Product {
  constructor() {}

  async getProducts(): Promise<any[]> {
    const apiUrl: string = 'http://127.0.0.1:5000/api/products';
    let products: any[] = [];
    $.LoadingOverlay('show', {
      background: 'rgb(0 0 0 0)',
    });

    await axios
      .get(apiUrl)
      .then((response: any) => {
        products = response.data;
      })
      .catch((error: any) => {
        console.log(error);
      })
      .finally(() => {
        //
        $.LoadingOverlay('hide');
      });

    return products;
  }
}
