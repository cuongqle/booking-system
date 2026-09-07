import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { startWith } from 'rxjs';
import { BookingService } from '../booking.service';
import {
  BOOKING_STATUSES,
  BookingStatus,
  RESOURCE_TYPE_LABELS,
  Resource,
  formatMoney,
  quoteBookingTotal,
} from '../booking.models';
import { extractErrorMessage } from '../../../core/api/extract-error-message';
import { controlErrorMessage, showControlError } from '../../../core/forms/form-errors';
import { NotificationService } from '../../notifications/notification.service';
import {
  dateRangeValidator,
  fromDatetimeLocalValue,
  toDatetimeLocalValue,
} from '../date-range.validator';

@Component({
  selector: 'app-booking-edit-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './booking-edit.page.html',
  host: { class: 'page-shell' },
})
export class BookingEditPage implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly bookingService = inject(BookingService);
  private readonly notifications = inject(NotificationService);
  private readonly router = inject(Router);

  readonly statuses = BOOKING_STATUSES;
  readonly resources = signal<Resource[]>([]);
  readonly bookingId = signal<number | null>(null);
  readonly loading = signal(true);
  readonly submitting = signal(false);
  readonly error = signal<string | null>(null);
  readonly showError = showControlError;
  readonly errorMessage = controlErrorMessage;
  readonly typeLabel = RESOURCE_TYPE_LABELS;
  readonly formatMoney = formatMoney;

  readonly form = this.fb.nonNullable.group(
    {
      resourceId: ['', [Validators.required]],
      startDate: ['', [Validators.required]],
      endDate: ['', [Validators.required]],
      status: ['PENDING' as BookingStatus, [Validators.required]],
    },
    { validators: [dateRangeValidator()] },
  );

  private readonly formValue = toSignal(
    this.form.valueChanges.pipe(startWith(this.form.getRawValue())),
    { initialValue: this.form.getRawValue() },
  );

  readonly selectedResource = computed(() => {
    const id = this.formValue().resourceId;
    return this.resources().find((resource) => resource.id === id);
  });

  readonly quotedTotal = computed(() => {
    const value = this.formValue();
    return quoteBookingTotal(this.selectedResource(), value.startDate ?? '', value.endDate ?? '');
  });

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isFinite(id)) {
      this.error.set('Invalid booking id');
      this.loading.set(false);
      return;
    }

    this.bookingId.set(id);

    this.bookingService.getResources().subscribe({
      next: (resources) => this.resources.set(resources),
      error: (err) => this.error.set(extractErrorMessage(err, 'Failed to load resources')),
    });

    this.bookingService.getBooking(id).subscribe({
      next: (booking) => {
        this.form.patchValue({
          resourceId: booking.resourceId,
          startDate: toDatetimeLocalValue(booking.startDate),
          endDate: toDatetimeLocalValue(booking.endDate),
          status: booking.status,
        });
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(extractErrorMessage(err, 'Failed to load booking'));
        this.loading.set(false);
      },
    });
  }

  submit(): void {
    const id = this.bookingId();
    if (id == null) {
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set(
        this.form.hasError('dateRange')
          ? 'Start time must be before end time'
          : 'Please fix the highlighted fields',
      );
      return;
    }

    this.submitting.set(true);
    this.error.set(null);

    const raw = this.form.getRawValue();
    this.bookingService
      .updateBooking(id, {
        resourceId: raw.resourceId,
        startDate: fromDatetimeLocalValue(raw.startDate),
        endDate: fromDatetimeLocalValue(raw.endDate),
        status: raw.status,
      })
      .subscribe({
        next: (booking) => {
          this.submitting.set(false);
          this.notifications.refresh();
          void this.router.navigate(['/bookings', booking.id]);
        },
        error: (err) => {
          this.submitting.set(false);
          this.error.set(extractErrorMessage(err, 'Failed to update booking'));
        },
      });
  }
}
