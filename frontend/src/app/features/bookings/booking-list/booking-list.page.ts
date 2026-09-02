import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BookingService } from '../booking.service';
import { Booking, bookingStatusMeta } from '../booking.models';
import { extractErrorMessage } from '../../../core/api/extract-error-message';
import { formatBookingRange } from '../calendar.utils';

@Component({
  selector: 'app-booking-list-page',
  imports: [RouterLink],
  templateUrl: './booking-list.page.html',
  host: { class: 'page-shell' },
})
export class BookingListPage implements OnInit {
  private readonly bookingService = inject(BookingService);

  readonly bookings = signal<Booking[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly formatRange = formatBookingRange;
  readonly statusMeta = bookingStatusMeta;

  ngOnInit(): void {
    this.bookingService.getBookings().subscribe({
      next: (bookings) => {
        this.bookings.set(bookings);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(extractErrorMessage(err, 'Failed to load bookings'));
        this.loading.set(false);
      },
    });
  }
}
