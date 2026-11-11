import { ChangeDetectorRef, Component } from '@angular/core';
import { ProductCard } from '../product-card/product-card';
import { Product } from '../services/product';

@Component({
  selector: 'app-product',
  imports: [ProductCard],
  templateUrl: './product.html',
  styleUrl: './product.css',
})
export class ProductComponent {
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
