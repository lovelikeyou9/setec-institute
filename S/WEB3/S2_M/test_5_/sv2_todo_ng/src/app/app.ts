import { Component } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { MainSlider } from './main-slider/main-slider';
import { ProductCard } from './product-card/product-card';
import {
  NgStyle, UpperCasePipe, LowerCasePipe, DatePipe, CurrencyPipe, DecimalPipe, PercentPipe, SlicePipe, JsonPipe, TitleCasePipe

} from '@angular/common';
import { KhmerCurrencyPipe } from './khmer-currency-in-pipe';
import { CartService } from './service/cart-service';


@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet, MainSlider, ProductCard, NgStyle, UpperCasePipe, LowerCasePipe, DatePipe, CurrencyPipe,
    DecimalPipe, PercentPipe, SlicePipe, JsonPipe, TitleCasePipe, KhmerCurrencyPipe, RouterLink
  ],

  templateUrl: './app.html',
  styleUrl: './app.css'

})

/* 1
export class App {
  title: string = 'sv2_todo_ng';
  std_name: string = 'Bora';
  age: number = 25;
  project_url: string = 'https://files.shapes.inc/api/files/avatar_f10911e8-d770-4f14-8819-5a2f2cabfa07.png';

} */

/* 2
export class App {

  image_array: string[] = [
      '3bdd27.jpg',
      'maxresdefault.jpg',
      'Two-Soyjacks-Transparent-meme-5.png',
      'draco-malfoy-harry-potter.gif',
      'Leo-DiCaprio-in-Django-Unchained.avif',
      'Patrick-Bateman-Sigma-Face-Meme-Template-1.webp',
      'png-transparent-jay-gatsby-leonardo-dicaprio-the-great-gatsby-hollywood-meme-leonardo-dicaprio-celebrities-culture-film-thumbnail.png'

  ];
  
  images: string[] = [];

  

  is_disable: boolean = false;

  height: number = 200;
  width: number = 200;
  show_image: boolean = true;

  constructor() {
    this.images = [];
  }
  
  changeImage(): void{

    this.show_image = false;
    this.images = [];


    for(let i = 0; i < 5; i++ ){
      const random_index: number = Math.floor(Math.random() * this.image_array.length);
      this.images.push(this.image_array[random_index]);
    }


  }
  
  changeDisable(): void{
    this.is_disable = !this.is_disable;
  }

  addSize(): void{
    this.height += 10;
    this.width += 10;
  }

  removeSize(): void{
    this.height -= 10;
    this.width -= 10;
  }

  toggleImage(): void{
    this.show_image = !this.show_image;
  }

} 

*/



/*
export class App {

  image: string = 'draco-malfoy-harry-potter.gif';
  std_name:string = 'vireak dara';
  date:Date = new Date();
  std_dob:Date = this.date;
  salary:number = 1000000;
  json_data:any = {
    "id": 1,
    "title": "Fjallraven - Foldsack No. 1 Backpack, Fits 15 Laptops",
    "price": 109.95,
    "description": "Your perfect pack for everyday use and walks in the forest. Stash your laptop (up to 15 inches) in the padded sleeve, your everyday",
    "category": "men's clothing",
    "image": "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
    "rating": {
      "rate": 3.9,
      "count": 120
    }
  }



  Math = Math;


  

}

*/



export class App {


  constructor(public cartService: CartService){}
  
  image: string = '3bdd27.jpg';

  

  
}