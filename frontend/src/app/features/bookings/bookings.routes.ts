import { Routes } from '@angular/router';
import { authGuard } from '../../core/auth/auth.guard';

export const BOOKINGS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./booking-list/booking-list.page').then((m) => m.BookingListPage),
  },
  {
    path: 'calendar',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./booking-calendar/booking-calendar.page').then((m) => m.BookingCalendarPage),
  },
  {
    path: 'new',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./booking-create/booking-create.page').then((m) => m.BookingCreatePage),
  },
  {
    path: ':id/edit',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./booking-edit/booking-edit.page').then((m) => m.BookingEditPage),
  },
  {
    path: ':id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./booking-detail/booking-detail.page').then((m) => m.BookingDetailPage),
  },
];
