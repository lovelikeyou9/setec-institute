/* 1

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  
  name: 'khmerCurrency'
})
export class KhmerCurrencyPipe implements PipeTransform {

  transform(value: string, case_:string): number {
    if (case_ == 'in'){
      return (Math.ceil(parseFloat(value)/100))*100;
    }else{
      return (Math.floor(parseFloat(value)/100))*100;
    }
  }

}

*/

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  
  name: 'khmerCurrency'
})
export class KhmerCurrencyPipe implements PipeTransform {

  transform(value: number, case_:string = 'in'): string {
    const exchangeRate = 4100;
    let riel = value * exchangeRate;

    if (case_ === 'in'){
      riel = Math.ceil(riel / 100) * 100;
    }else{
      riel = Math.floor(riel / 100) * 100;
    }

    return riel.toLocaleString('en-US') + '៛';
    
  }

}
