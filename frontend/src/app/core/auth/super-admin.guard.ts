import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const superAdminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAuthenticated() && auth.isSuperAdmin()) {
    return true;
  }

  if (!auth.isAuthenticated()) {
    return router.createUrlTree(['/auth/login']);
  }

  return router.createUrlTree(['/bookings']);
};
