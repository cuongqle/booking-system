import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { BookingService } from '../booking.service';
import { Room } from '../booking.models';
import { extractErrorMessage } from '../../../core/api/extract-error-message';
import { controlErrorMessage, showControlError } from '../../../core/forms/form-errors';
import { dateRangeValidator, fromDatetimeLocalValue } from '../date-range.validator';

@Component({
  selector: 'app-booking-create-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './booking-create.page.html',
  host: { class: 'page-shell page-shell--center' },
})
export class BookingCreatePage implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly bookingService = inject(BookingService);
  private readonly router = inject(Router);

  readonly rooms = signal<Room[]>([]);
  readonly loadingRooms = signal(true);
  readonly error = signal<string | null>(null);
  readonly submitting = signal(false);
  readonly showError = showControlError;
  readonly errorMessage = controlErrorMessage;

  readonly form = this.fb.nonNullable.group(
    {
      roomId: ['', [Validators.required]],
      startDate: ['', [Validators.required]],
      endDate: ['', [Validators.required]],
    },
    { validators: [dateRangeValidator()] },
  );

  ngOnInit(): void {
    this.bookingService.getRooms().subscribe({
      next: (rooms) => {
        this.rooms.set(rooms);
        this.loadingRooms.set(false);
      },
      error: (err) => {
        this.error.set(extractErrorMessage(err, 'Failed to load rooms'));
        this.loadingRooms.set(false);
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
        roomId: raw.roomId,
        startDate: fromDatetimeLocalValue(raw.startDate),
        endDate: fromDatetimeLocalValue(raw.endDate),
      })
      .subscribe({
        next: (booking) => {
          this.submitting.set(false);
          void this.router.navigate(['/bookings', booking.id]);
        },
        error: (err) => {
          this.submitting.set(false);
          this.error.set(extractErrorMessage(err, 'Failed to create booking'));
        },
      });
  }
}
