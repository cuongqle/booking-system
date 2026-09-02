import { Routes } from '@angular/router';
import { guestGuard } from '../../core/auth/guest.guard';

export const AUTH_ROUTES: Routes = [
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () => import('./login/login.page').then((m) => m.LoginPage),
  },
  {
    path: 'register',
    canActivate: [guestGuard],
    loadComponent: () => import('./register/register.page').then((m) => m.RegisterPage),
  },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
];
