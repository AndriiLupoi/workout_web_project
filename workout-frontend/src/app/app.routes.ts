import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/plans',
    pathMatch: 'full'
  },
  {
    path: 'auth',
    loadChildren: () =>
      import('./features/auth/auth-routing-module')
        .then(m => m.AUTH_ROUTES)
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadChildren: () =>
      import('./features/profile/profile-routing-module')
        .then(m => m.PROFILE_ROUTES)
  },
  {
    path: 'plans',
    canActivate: [authGuard],
    loadChildren: () =>
      import('./features/workout-plan/workout-plan-routing-module')
        .then(m => m.WORKOUT_PLAN_ROUTES)
  },
  {
    path: 'exercises',
    canActivate: [authGuard],
    loadChildren: () =>
      import('./features/exercises/exercises-routing-module')
        .then(m => m.EXERCISES_ROUTES)
  }
];
