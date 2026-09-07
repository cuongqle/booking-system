import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiUrl } from '../../core/api/api-url';
import {
  Booking,
  BookingStatus,
  BookingUpdateRequest,
  BookingWriteRequest,
  Invoice,
  Resource,
} from './booking.models';

export interface BookingListFilters {
  status?: BookingStatus | '';
  resourceId?: string;
  userId?: string;
}

@Injectable({ providedIn: 'root' })
export class BookingService {
  private readonly http = inject(HttpClient);

  getBookings(filters: BookingListFilters = {}): Observable<Booking[]> {
    let params = new HttpParams();
    if (filters.status) {
      params = params.set('status', filters.status);
    }
    if (filters.resourceId?.trim()) {
      params = params.set('resourceId', filters.resourceId.trim());
    }
    return this.http.get<Booking[]>(apiUrl('/bookings'), { params });
  }

  listAllBookings(filters: BookingListFilters = {}): Observable<Booking[]> {
    let params = new HttpParams();
    if (filters.status) {
      params = params.set('status', filters.status);
    }
    if (filters.resourceId?.trim()) {
      params = params.set('resourceId', filters.resourceId.trim());
    }
    if (filters.userId?.trim()) {
      params = params.set('userId', filters.userId.trim());
    }
    return this.http.get<Booking[]>(apiUrl('/admin/bookings'), { params });
  }

  getBooking(id: number): Observable<Booking> {
    return this.http.get<Booking>(apiUrl(`/bookings/${id}`));
  }

  getInvoice(bookingId: number): Observable<Invoice> {
    return this.http.get<Invoice>(apiUrl(`/bookings/${bookingId}/invoice`));
  }

  payBooking(bookingId: number): Observable<Invoice> {
    return this.http.post<Invoice>(apiUrl(`/bookings/${bookingId}/pay`), {});
  }

  downloadInvoicePdf(bookingId: number): Observable<Blob> {
    return this.http.get(apiUrl(`/bookings/${bookingId}/invoice/pdf`), {
      responseType: 'blob',
    });
  }

  getResources(): Observable<Resource[]> {
    return this.http.get<Resource[]>(apiUrl('/resources'));
  }

  createBooking(payload: BookingWriteRequest): Observable<Booking> {
    return this.http.post<Booking>(apiUrl('/bookings'), payload);
  }

  updateBooking(id: number, payload: BookingUpdateRequest): Observable<Booking> {
    return this.http.put<Booking>(apiUrl(`/bookings/${id}`), payload);
  }
}
