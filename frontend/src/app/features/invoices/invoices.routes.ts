import { Routes } from '@angular/router';
import { authGuard } from '../../core/auth/auth.guard';

export const INVOICES_ROUTES: Routes = [
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./invoice-list/invoice-list.page').then((m) => m.InvoiceListPage),
  },
];
