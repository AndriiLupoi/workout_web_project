// admin-routing-module.ts
import { Routes } from '@angular/router';
import { AdminUsersComponent } from './admin-users';
import { inject } from '@angular/core';
import { AuthService } from '../../core/services/auth';
import { Router } from '@angular/router';

// Guard — пускає тільки ADMIN і OWNER
export const adminGuard = () => {
  const auth   = inject(AuthService);
  const router = inject(Router);
  if (auth.isAdmin()) return true;
  router.navigate(['/plans']);
  return false;
};

// Guard — пускає тільки OWNER
export const ownerGuard = () => {
  const auth   = inject(AuthService);
  const router = inject(Router);
  if (auth.isOwner()) return true;
  router.navigate(['/plans']);
  return false;
};

export const adminRoutes: Routes = [
  {
    path: 'users',
    component: AdminUsersComponent,
    canActivate: [adminGuard]
  }
];
