import { Routes } from '@angular/router';

export const WORKOUT_PLAN_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./workout-plan').then(m => m.WorkoutPlanComponent)
  }
];
