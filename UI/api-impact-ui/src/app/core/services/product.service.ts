import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';
import { ApiTrackerService } from '../api-tracker.service';

// @Injectable({ providedIn: 'root' })
// export class ProductService {

//   private BASE_URL = 'http://localhost:8081';

//   constructor(private http: HttpClient) {}

//   getProducts(): Observable<Product[]> {
//     return this.http.get<Product[]>(`${this.BASE_URL}/api/products`);
//   }

//   getProductById(id: number): Observable<Product> {
//     return this.http.get<Product>(
//       `${this.BASE_URL}/api/products/${id}`
//     );
//   }
// }

@Injectable({ providedIn: 'root' })
export class ProductService {
   private BASE_URL = 'http://localhost:8081';
  // Keep the same API base only in ApiTrackerService to avoid duplication.
  constructor(private apiTracker: ApiTrackerService,
    private http: HttpClient
   ) {}

  getProducts(): Observable<Product[]> {
    // IMPORTANT: use relative path (the ApiTrackerService will prepend BASE_URL)
    return this.apiTracker.get('/api/products', 'HomeComponent');
  }

  // getProductById(id: number): Observable<Product> {
  //   return this.http.get(`/api/products/${id}`, 'ProductDetailComponent');
  // }
  
getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.BASE_URL}/api/products/${id}`)

}
}



// @Injectable({ providedIn: 'root' })
// export class ProductService {
//   constructor(private apiTracker: ApiTrackerService) {}

//   getProducts(): Observable<Product[]> {
//     return this.apiTracker.get<Product[]>('/api/products', 'HomeComponent');
//   }

//   getProductById(id: number): Observable<Product> {
//     return this.apiTracker.get<Product>(`/api/products/${id}`, 'ProductDetailComponent');
//   }
// }

