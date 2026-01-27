import { Component, OnInit } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { ImpactService } from './core/services/impact.service';
import { ImpactHighlightDirective } from './core/directives/impact-highlight.directive';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, ImpactHighlightDirective],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
   title = 'api-impact-ui';
  constructor(public impact: ImpactService) {}

  ngOnInit() {
    this.impact.loadReport();
  }

  toggleImpactMode() {
    this.impact.toggle();
  }
}

