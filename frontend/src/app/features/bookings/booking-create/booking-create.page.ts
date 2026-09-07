import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { startWith } from 'rxjs';
import { BookingService } from '../booking.service';
import {
  RESOURCE_TYPE_LABELS,
  Resource,
  formatMoney,
  quoteBookingTotal,
} from '../booking.models';
import { extractErrorMessage } from '../../../core/api/extract-error-message';
import { controlErrorMessage, showControlError } from '../../../core/forms/form-errors';
import { dateRangeValidator, fromDatetimeLocalValue } from '../date-range.validator';
import { NotificationService } from '../../notifications/notification.service';

@Component({
  selector: 'app-booking-create-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './booking-create.page.html',
  host: { class: 'page-shell page-shell--center' },
})
export class BookingCreatePage implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly bookingService = inject(BookingService);
  private readonly notifications = inject(NotificationService);
  private readonly router = inject(Router);

  readonly resources = signal<Resource[]>([]);
  readonly loadingResources = signal(true);
  readonly error = signal<string | null>(null);
  readonly submitting = signal(false);
  readonly showError = showControlError;
  readonly errorMessage = controlErrorMessage;
  readonly typeLabel = RESOURCE_TYPE_LABELS;
  readonly formatMoney = formatMoney;

  readonly form = this.fb.nonNullable.group(
    {
      resourceId: ['', [Validators.required]],
      startDate: ['', [Validators.required]],
      endDate: ['', [Validators.required]],
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
    this.bookingService.getResources().subscribe({
      next: (resources) => {
        this.resources.set(resources);
        this.loadingResources.set(false);
      },
      error: (err) => {
        this.error.set(extractErrorMessage(err, 'Failed to load resources'));
        this.loadingResources.set(false);
      },
    });
  }

  submit(): void {
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
      .createBooking({
        resourceId: raw.resourceId,
        startDate: fromDatetimeLocalValue(raw.startDate),
        endDate: fromDatetimeLocalValue(raw.endDate),
      })
      .subscribe({
        next: (booking) => {
          this.submitting.set(false);
          this.notifications.refresh();
          void this.router.navigate(['/bookings', booking.id]);
        },
        error: (err) => {
          this.submitting.set(false);
          this.error.set(extractErrorMessage(err, 'Failed to create booking'));
        },
      });
  }
}
