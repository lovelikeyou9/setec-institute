import { ChangeDetectorRef, Component, signal } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { Slider } from './slider/slider';
import { ProductCard } from './product-card/product-card';
import { FormsModule } from '@angular/forms';



@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Slider, ProductCard, FormsModule, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {}
