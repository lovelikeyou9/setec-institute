import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'toKhr',
})
export class ToKhrPipe implements PipeTransform {
  transform(value: number): string {
    let total = Math.ceil((value * 4100) / 100) * 100;
    return total.toLocaleString();
  }
}
