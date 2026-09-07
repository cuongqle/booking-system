import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./features/home/home.routes').then((m) => m.HOME_ROUTES),
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then((m) => m.AUTH_ROUTES),
  },
  {
    path: 'bookings',
    loadChildren: () => import('./features/bookings/bookings.routes').then((m) => m.BOOKINGS_ROUTES),
  },
  {
    path: 'invoices',
    loadChildren: () =>
      import('./features/invoices/invoices.routes').then((m) => m.INVOICES_ROUTES),
  },
  {
    path: 'resources',
    loadChildren: () =>
      import('./features/resources/resources.routes').then((m) => m.RESOURCES_ROUTES),
  },
  { path: '**', redirectTo: '' },
];
