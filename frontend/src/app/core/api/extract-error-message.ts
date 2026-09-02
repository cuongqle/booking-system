import { HttpErrorResponse } from '@angular/common/http';
import { ErrorResponse } from '../models/error-response';

export function extractErrorMessage(error: unknown, fallback = 'Something went wrong'): string {
  if (error instanceof HttpErrorResponse) {
    const body = error.error as ErrorResponse | string | null;
    if (body && typeof body === 'object' && 'message' in body && body.message) {
      return body.message;
    }
    if (typeof body === 'string' && body.trim()) {
      return body;
    }
    return error.message || fallback;
  }
  return fallback;
}
