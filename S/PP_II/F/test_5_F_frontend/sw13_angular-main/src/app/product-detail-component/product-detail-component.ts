import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-product-detail-component',
  imports: [],
  templateUrl: './product-detail-component.html',
  styleUrl: './product-detail-component.css',
})
export class ProductDetailComponent {
  private route = inject(ActivatedRoute);
  pro_name: string | null = '';
  price: string | null = '';
  description: string | null = '';
  image: string | null = '';

  ngOnInit(): void {
    this.pro_name = this.route.snapshot.queryParamMap.get('name');
    this.price = this.route.snapshot.queryParamMap.get('price');
    this.description = this.route.snapshot.queryParamMap.get('description');
    this.image = this.route.snapshot.queryParamMap.get('image');
  }
}
