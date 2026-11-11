import { ChangeDetectorRef, Component } from '@angular/core';
import { ProductCard } from '../product-card/product-card';
import { Slider } from '../slider/slider';
import { Product } from '../services/product';

@Component({
  selector: 'app-home',
  imports: [ProductCard, Slider],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class HomeComponent {
  constructor(
    public products: Product,
    private cdr: ChangeDetectorRef,
  ) {}

  product_list: any = [];
  async ngOnInit() {
    this.product_list = await this.products.getProducts();
    this.cdr.detectChanges();
  }
}
