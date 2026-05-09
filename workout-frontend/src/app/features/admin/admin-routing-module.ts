// admin-routing-module.ts
import { Routes } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../../core/services/auth';
import { Router } from '@angular/router';
import {AdminAuditComponent} from './admin-audit/admin-audit';
import {AdminUsersComponent} from './admin-user/admin-users';

export const adminGuard = () => {
  const auth   = inject(AuthService);
  const router = inject(Router);
  if (auth.isAdmin()) return true;
  router.navigate(['/plans']);
  return false;
};

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
  },
  {
    path: 'audit',
    component: AdminAuditComponent,
    canActivate: [ownerGuard]
  }
];
