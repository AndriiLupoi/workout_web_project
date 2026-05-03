import { Routes } from '@angular/router';

export const PROGRESS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./progress').then(m => m.ProgressComponent)
  }
];
