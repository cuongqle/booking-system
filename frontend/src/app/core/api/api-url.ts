import { environment } from '../../../environments/environment';

/**
 * Builds a full API URL with the global versioned prefix.
 * Pass resource paths only, e.g. apiUrl('/bookings') -> /api/v1/bookings
 */
export function apiUrl(path: string): string {
  const resourcePath = path.startsWith('/') ? path : `/${path}`;
  const prefix = environment.apiPrefix.replace(/\/$/, '');
  return `${environment.apiBaseUrl}${prefix}${resourcePath}`;
}
