
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';

export interface ImpactItem {
  component: string;
  api: string;
  risk: number;
}

@Injectable({ providedIn: 'root' })
export class ImpactService {
  private enabled$ = new BehaviorSubject<boolean>(false);
  enabledObs$ = this.enabled$.asObservable();

  private impactMap = new Map<string, ImpactItem>();

  constructor(private http: HttpClient) {}

  private impactChanged$ = new BehaviorSubject<void>(undefined);
get impactChangedObs$() { return this.impactChanged$.asObservable(); }

loadReport() {
  this.http.get<any>('/test-report.json').subscribe({
    next: (res) => {
      const items: ImpactItem[] = res?.impactedComponents ?? [];
      this.impactMap = new Map(items.map(i => [i.component, i]));
      this.impactChanged$.next(); // notify
    },
    error: (err) => console.error('Failed to load test-report.json', err)
  });
}


  /** Load once on app startup */
  // loadReport() {
  //   // If Angular is served from Spring Boot same origin:
  //   // use '/risk-report.json'
  //   // If Angular is on 4200, use proxy or full URL.
  //   this.http.get<any>('/test-report.json').subscribe({
  //     next: (res) => {
  //       const items: ImpactItem[] = res?.impactedComponents ?? [];
  //       console.log(' items: ',items);
  //       this.impactMap = new Map(items.map(i => [i.component, i]));
  //       console.log(' Risk report loaded:', items.length);
  //     },
  //     error: (err) => console.error(' Failed to load test-report.json', err)
  //   });
  // }

  toggle() {
    this.enabled$.next(!this.enabled$.value);
  }

  isEnabled() {
    return this.enabled$.value;
  }

  getImpact(componentKey: string) {
    return this.impactMap.get(componentKey);
  }
}

