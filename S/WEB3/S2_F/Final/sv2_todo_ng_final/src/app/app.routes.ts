import { Routes } from '@angular/router';
import { Home } from './home/home';
import { Product } from './product/product';
import { Cart } from './cart/cart';
import { ProductDetail } from './product-detail/product-detail';
import { CheckOut } from './check-out/check-out';
import { Blog } from './blog/blog';
import { Error404 } from './error404/error404';


export const routes: Routes = [
    { path: '', component: Home},
    { path: 'home', component: Home},
    { path: 'product', component: Product},
    { path: 'cart', component: Cart},
    { path: 'product-detail', component: ProductDetail},
    { path: 'check-out', component: CheckOut},
    { path: 'blog', component: Blog},
    { path: '**', component: Error404}
];
