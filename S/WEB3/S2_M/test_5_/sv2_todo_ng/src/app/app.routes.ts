import { Routes } from '@angular/router';
import { Home } from './home/home';
import { Product } from './product/product';
import { Cart } from './cart/cart';
import { Error404 } from './error404/error404';
import { ProductDetail } from './product-detail/product-detail';


export const routes: Routes = 
[

    { path: '', component: Home },
    { path: 'home', component: Home },
    { path: 'product', component: Product },
    { path: 'cart', component: Cart },
    { path: '**', component: Error404 },
    { path: 'product-detail', component: ProductDetail }


];
