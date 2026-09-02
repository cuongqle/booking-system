import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { BookingService } from '../booking.service';
import { Booking, bookingStatusMeta } from '../booking.models';
import { extractErrorMessage } from '../../../core/api/extract-error-message';

@Component({
  selector: 'app-booking-detail-page',
  imports: [RouterLink, DatePipe],
  templateUrl: './booking-detail.page.html',
  host: { class: 'page-shell' },
})
export class BookingDetailPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly bookingService = inject(BookingService);

  readonly booking = signal<Booking | null>(null);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly statusMeta = bookingStatusMeta;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isFinite(id)) {
      this.error.set('Invalid booking id');
      this.loading.set(false);
      return;
    }

    this.bookingService.getBooking(id).subscribe({
      next: (booking) => {
        this.booking.set(booking);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(extractErrorMessage(err, 'Failed to load booking'));
        this.loading.set(false);
      },
    });
  }
}
