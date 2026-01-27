
import { Directive, ElementRef, Input, OnDestroy, OnInit, Renderer2 } from '@angular/core';
import { Subscription,merge } from 'rxjs';
import { ImpactService } from '../services/impact.service';

@Directive({
  selector: '[impactHighlight]',
  standalone: true
})
export class ImpactHighlightDirective implements OnInit, OnDestroy {
  @Input('impactHighlight') componentKey!: string;

  private sub!: Subscription;

  constructor(
    private el: ElementRef,
    private renderer: Renderer2,
    private impact: ImpactService
  ) {}

  ngOnInit() {
  // 1) Ensure you call impact.loadReport() somewhere on app startup (see below)
  this.sub = merge(
      this.impact.enabledObs$,
      this.impact.impactChangedObs$
    ).subscribe(() => {
      this.clear();
      const item = this.impact.getImpact(this.componentKey);
      if (this.impact.isEnabled() && item) {
        this.apply(item.api, item.risk);
      }
    });
}

  // ngOnInit() {
  //   this.sub = this.impact.enabledObs$.subscribe(enabled => {
  //     this.clear();
  //     const item = this.impact.getImpact(this.componentKey);
  //     if (enabled && item) {
  //       this.apply(item.api, item.risk);
  //     }
  //   });
  // }

  private apply(api: string, risk: number) {
    const color = risk >= 0.8 ? '#dc2626' : risk >= 0.5 ? '#f59e0b' : '#10b981';

    this.renderer.setStyle(this.el.nativeElement, 'outline', `3px solid ${color}`);
    this.renderer.setStyle(this.el.nativeElement, 'border-radius', '6px');
    this.renderer.setStyle(this.el.nativeElement, 'position', 'relative');

    const badge = this.renderer.createElement('div');
    this.renderer.addClass(badge, 'impact-badge');
    this.renderer.setStyle(badge, 'background', color);
    badge.innerText = `API: ${api} | Risk: ${risk}`;

    this.renderer.appendChild(this.el.nativeElement, badge);
  }

  private clear() {
    this.renderer.removeStyle(this.el.nativeElement, 'outline');
    this.renderer.removeStyle(this.el.nativeElement, 'border-radius');
    this.renderer.removeStyle(this.el.nativeElement, 'position');

    const old = this.el.nativeElement.querySelector('.impact-badge');
    if (old) old.remove();
  }

  ngOnDestroy() {
    this.sub?.unsubscribe();
  }
}
