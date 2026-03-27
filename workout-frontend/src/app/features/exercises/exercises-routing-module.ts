import { Routes } from '@angular/router';

export const EXERCISES_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./exercises').then(m => m.ExercisesComponent)
  }
];
