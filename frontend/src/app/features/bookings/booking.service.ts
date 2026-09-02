import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiUrl } from '../../core/api/api-url';
import { Booking, BookingUpdateRequest, BookingWriteRequest, Room } from './booking.models';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private readonly http = inject(HttpClient);

  getBookings(): Observable<Booking[]> {
    return this.http.get<Booking[]>(apiUrl('/bookings'));
  }

  getBooking(id: number): Observable<Booking> {
    return this.http.get<Booking>(apiUrl(`/bookings/${id}`));
  }

  getRooms(): Observable<Room[]> {
    return this.http.get<Room[]>(apiUrl('/rooms'));
  }

  createBooking(payload: BookingWriteRequest): Observable<Booking> {
    return this.http.post<Booking>(apiUrl('/bookings'), payload);
  }

  updateBooking(id: number, payload: BookingUpdateRequest): Observable<Booking> {
    return this.http.put<Booking>(apiUrl(`/bookings/${id}`), payload);
  }
}
