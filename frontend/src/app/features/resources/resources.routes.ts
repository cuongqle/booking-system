import { Routes } from '@angular/router';
import { authGuard } from '../../core/auth/auth.guard';
import { adminGuard } from '../../core/auth/admin.guard';

export const RESOURCES_ROUTES: Routes = [
  {
    path: '',
    canActivate: [authGuard, adminGuard],
    loadComponent: () =>
      import('./resource-list/resource-list.page').then((m) => m.ResourceListPage),
  },
  {
    path: 'new',
    canActivate: [authGuard, adminGuard],
    loadComponent: () =>
      import('./resource-form/resource-form.page').then((m) => m.ResourceFormPage),
  },
  {
    path: ':id/edit',
    canActivate: [authGuard, adminGuard],
    loadComponent: () =>
      import('./resource-form/resource-form.page').then((m) => m.ResourceFormPage),
  },
];
