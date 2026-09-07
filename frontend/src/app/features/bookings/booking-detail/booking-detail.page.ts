import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { BookingService } from '../booking.service';
import {
  Booking,
  RESOURCE_TYPE_LABELS,
  Resource,
  bookingStatusMeta,
  formatMoney,
} from '../booking.models';
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
  readonly resource = signal<Resource | null>(null);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly statusMeta = bookingStatusMeta;
  readonly formatMoney = formatMoney;
  readonly typeLabel = RESOURCE_TYPE_LABELS;

  readonly durationLabel = computed(() => {
    const item = this.booking();
    if (!item) {
      return null;
    }
    const start = new Date(item.startDate).getTime();
    const end = new Date(item.endDate).getTime();
    if (!Number.isFinite(start) || !Number.isFinite(end) || end <= start) {
      return null;
    }
    const minutes = Math.round((end - start) / 60_000);
    if (minutes < 60) {
      return `${minutes} min`;
    }
    const hours = Math.floor(minutes / 60);
    const rem = minutes % 60;
    return rem === 0 ? `${hours} hr` : `${hours} hr ${rem} min`;
  });

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isFinite(id)) {
      this.error.set('Invalid booking id');
      this.loading.set(false);
      return;
    }

    forkJoin({
      booking: this.bookingService.getBooking(id),
      resources: this.bookingService.getResources().pipe(catchError(() => of([] as Resource[]))),
    }).subscribe({
      next: ({ booking, resources }) => {
        this.booking.set(booking);
        this.resource.set(resources.find((item) => item.id === booking.resourceId) ?? null);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(extractErrorMessage(err, 'Failed to load booking'));
        this.loading.set(false);
      },
    });
  }
}
