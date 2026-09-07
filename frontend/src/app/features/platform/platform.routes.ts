import { Routes } from '@angular/router';
import { authGuard } from '../../core/auth/auth.guard';
import { superAdminGuard } from '../../core/auth/super-admin.guard';

export const PLATFORM_ROUTES: Routes = [
  {
    path: 'organizations',
    canActivate: [authGuard, superAdminGuard],
    loadComponent: () =>
      import('./organization-list/organization-list.page').then((m) => m.OrganizationListPage),
  },
  {
    path: 'organizations/:id/users',
    canActivate: [authGuard, superAdminGuard],
    loadComponent: () =>
      import('./organization-users/organization-users.page').then((m) => m.OrganizationUsersPage),
  },
  { path: '', pathMatch: 'full', redirectTo: 'organizations' },
];
