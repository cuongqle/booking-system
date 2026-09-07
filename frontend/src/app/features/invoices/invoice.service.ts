import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiUrl } from '../../core/api/api-url';
import { Invoice, InvoiceListFilters, InvoiceStatus } from '../bookings/booking.models';

@Injectable({ providedIn: 'root' })
export class InvoiceService {
  private readonly http = inject(HttpClient);

  listMine(status?: InvoiceStatus | ''): Observable<Invoice[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<Invoice[]>(apiUrl('/invoices'), { params });
  }

  listAll(filters: InvoiceListFilters = {}): Observable<Invoice[]> {
    let params = new HttpParams();
    if (filters.status) {
      params = params.set('status', filters.status);
    }
    if (filters.userId?.trim()) {
      params = params.set('userId', filters.userId.trim());
    }
    if (filters.bookingId?.trim()) {
      params = params.set('bookingId', filters.bookingId.trim());
    }
    return this.http.get<Invoice[]>(apiUrl('/admin/invoices'), { params });
  }
}
