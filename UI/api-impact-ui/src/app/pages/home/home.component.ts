// // import { Component } from '@angular/core';

// // @Component({
// //   selector: 'app-home',
// //   imports: [],
// //   templateUrl: './home.component.html',
// //   styleUrl: './home.component.css'
// // })
// // export class HomeComponent {

// // }
// import { Component } from '@angular/core';
// import { ApiTrackerService } from '../../core/api-tracker.service';
// import { CommonModule } from '@angular/common';
// @Component({
//   selector: 'app-home',
//   standalone: true,
//   imports: [CommonModule], 
//   templateUrl: './home.component.html'
// })
// export class HomeComponent {

//    products: any[] | null = null;

//   constructor(private api: ApiTrackerService) {}

//   loadProducts() {
//     this.api.get('/api/products', 'HomeComponent')
//       .subscribe({
//         next: (res: any) => {
//           console.log('Products response:', res);
//           this.products = res;
//         },
//         error: (err: any) => console.error(err)
//       });
//   }
// }
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProductService } from '../../core/services/product.service';
import { Product } from '../../core/models/product.model';
import { ImpactHighlightDirective } from '../../core/directives/impact-highlight.directive';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule,ImpactHighlightDirective],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  carouselImages = [
    'assets/image1.jpg',
    'assets/image2.jpeg',

  'assets/image4.jpg'
   
];

currentIndex = 0;

next() {
  this.currentIndex =
    (this.currentIndex + 1) % this.carouselImages.length;
}

prev() {
  this.currentIndex =
    (this.currentIndex - 1 + this.carouselImages.length) %
    this.carouselImages.length;
}


  products: Product[] = [];

  constructor(
    private productService: ProductService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    this.productService.getProducts().subscribe({
      next: (res) => this.products = res,
      error: (err) => console.error(err)
    });
  }

  viewProduct(id: number) {
    this.router.navigate(['/product', id]);
  }
}

