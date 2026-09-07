import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BookingService } from '../booking.service';
import {
  BOOKING_STATUSES,
  Booking,
  BookingStatus,
  Resource,
  bookingStatusMeta,
  formatMoney,
} from '../booking.models';
import { extractErrorMessage } from '../../../core/api/extract-error-message';
import { formatBookingRange } from '../calendar.utils';

@Component({
  selector: 'app-booking-list-page',
  imports: [RouterLink, FormsModule],
  templateUrl: './booking-list.page.html',
  host: { class: 'page-shell page-shell--fill' },
})
export class BookingListPage implements OnInit {
  private readonly bookingService = inject(BookingService);

  readonly bookings = signal<Booking[]>([]);
  readonly resources = signal<Resource[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly statusFilter = signal<BookingStatus | ''>('');
  readonly resourceFilter = signal('');
  readonly formatRange = formatBookingRange;
  readonly statusMeta = bookingStatusMeta;
  readonly formatMoney = formatMoney;
  readonly statusOptions = BOOKING_STATUSES;

  ngOnInit(): void {
    this.bookingService.getResources().subscribe({
      next: (resources) => this.resources.set(resources),
      error: () => this.resources.set([]),
    });
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.bookingService
      .getBookings({
        status: this.statusFilter(),
        resourceId: this.resourceFilter(),
      })
      .subscribe({
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

  applyFilters(): void {
    this.load();
  }

  clearFilters(): void {
    this.statusFilter.set('');
    this.resourceFilter.set('');
    this.load();
  }
}
