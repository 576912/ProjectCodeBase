// import { Component } from '@angular/core';

// @Component({
//   selector: 'app-cart',
//   imports: [],
//   templateUrl: './cart.component.html',
//   styleUrl: './cart.component.css'
// })
// export class CartComponent {

// }

import { Component } from '@angular/core';
import { ApiTrackerService } from '../../core/api-tracker.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule],
  templateUrl:'./cart.component.html'
})
export class CartComponent {
   cart: any[] | null = null;

  constructor(private api: ApiTrackerService) {}

  // loadCart() {
  //   this.api.get('/api/products', 'CartComponent')
  //     .subscribe();
  // }
    loadCart() {
    this.api.get('/api/products', 'CartComponent')
      .subscribe({
        next: (res: any) => {
          console.log('Products response:', res);
          this.cart = res;
        },
        error: (err: any) => console.error(err)
      });
  }
}
