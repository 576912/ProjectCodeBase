// import { Component } from '@angular/core';

// @Component({
//   selector: 'app-product-detail',
//   imports: [],
//   templateUrl: './product-detail.component.html',
//   styleUrl: './product-detail.component.css'
// })
// export class ProductDetailComponent {

// }
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../../core/services/product.service';
import { Product } from '../../core/models/product.model';

@Component({
  selector: 'app-product-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.css'
})
export class ProductDetailsComponent implements OnInit {

  product!: Product;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService
  ) {}

  ngOnInit() {
    // const id = Number(this.route.snapshot.paramMap.get('id'));

    // this.productService.getProductById(id).subscribe({
    //   next: (res) => this.product = res,
    //   error: (err) => console.error(err)
    // });
   const id = Number(this.route.snapshot.paramMap.get('id'));
this.productService.getProductById(id).subscribe({
  next: product => this.product = product,
  error: err => console.error(err)
});


  }
}

