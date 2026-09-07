import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { BookingService } from '../booking.service';
import {
  Booking,
  Invoice,
  RESOURCE_TYPE_LABELS,
  Resource,
  bookingStatusMeta,
  formatMoney,
  invoiceStatusMeta,
} from '../booking.models';
import { extractErrorMessage } from '../../../core/api/extract-error-message';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-booking-detail-page',
  imports: [RouterLink, DatePipe],
  templateUrl: './booking-detail.page.html',
  host: { class: 'page-shell' },
})
export class BookingDetailPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly bookingService = inject(BookingService);
  private readonly auth = inject(AuthService);

  readonly booking = signal<Booking | null>(null);
  readonly resource = signal<Resource | null>(null);
  readonly invoice = signal<Invoice | null>(null);
  readonly loading = signal(true);
  readonly paying = signal(false);
  readonly error = signal<string | null>(null);
  readonly payError = signal<string | null>(null);
  readonly statusMeta = bookingStatusMeta;
  readonly invoiceStatusMeta = invoiceStatusMeta;
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

  readonly canPay = computed(() => {
    const booking = this.booking();
    const invoice = this.invoice();
    const userId = this.auth.currentUser()?.userId;
    return (
      !!booking &&
      !!invoice &&
      !!userId &&
      booking.userId === userId &&
      invoice.status === 'UNPAID' &&
      booking.status !== 'CANCELLED'
    );
  });

  readonly isOwner = computed(() => {
    const booking = this.booking();
    const userId = this.auth.currentUser()?.userId;
    return !!booking && !!userId && booking.userId === userId;
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
      invoice: this.bookingService.getInvoice(id).pipe(catchError(() => of(null))),
      resources: this.bookingService.getResources().pipe(catchError(() => of([] as Resource[]))),
    }).subscribe({
      next: ({ booking, invoice, resources }) => {
        this.booking.set(booking);
        this.invoice.set(invoice);
        this.resource.set(resources.find((item) => item.id === booking.resourceId) ?? null);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(extractErrorMessage(err, 'Failed to load booking'));
        this.loading.set(false);
      },
    });
  }

  pay(): void {
    const booking = this.booking();
    if (!booking || this.paying()) {
      return;
    }
    this.paying.set(true);
    this.payError.set(null);
    this.bookingService.payBooking(booking.id).subscribe({
      next: (invoice) => {
        this.invoice.set(invoice);
        this.bookingService.getBooking(booking.id).subscribe({
          next: (updated) => {
            this.booking.set(updated);
            this.paying.set(false);
          },
          error: () => {
            this.booking.update((current) =>
              current ? { ...current, status: 'CONFIRMED' } : current,
            );
            this.paying.set(false);
          },
        });
      },
      error: (err) => {
        this.payError.set(extractErrorMessage(err, 'Payment failed'));
        this.paying.set(false);
      },
    });
  }
}
