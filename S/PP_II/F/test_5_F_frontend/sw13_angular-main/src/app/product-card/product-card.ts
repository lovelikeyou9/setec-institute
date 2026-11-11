import { Component, Input } from '@angular/core';
import { ToKhrPipe } from '../pipes/to-khr-pipe';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-product-card',
  imports: [ToKhrPipe, RouterLink],
  templateUrl: './product-card.html',
  styleUrl: './product-card.css',
})
export class ProductCard {
  @Input() image: string = '';
  @Input() name: string = '';
  @Input() description: string = '';
  @Input() price: string = '';
  protected readonly parseFloat = parseFloat;
}
