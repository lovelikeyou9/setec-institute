import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MiddleWeb } from './middle-web/middle-web';
import { EndPage } from './end-page/end-page';
import { RouterLink } from '@angular/router';
import { CartService } from './service/cart-service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MiddleWeb, EndPage, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  constructor(public cartService: CartService){

  }

  image: string = "";
  





}
