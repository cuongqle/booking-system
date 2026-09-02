import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { BookingService } from '../booking.service';
import { Booking, bookingStatusMeta } from '../booking.models';
import { extractErrorMessage } from '../../../core/api/extract-error-message';
import {
  CalendarDay,
  buildMonthGrid,
  formatBookingRange,
  monthLabel,
  overlapsDay,
  parseIsoDate,
  toIsoDate,
} from '../calendar.utils';

@Component({
  selector: 'app-booking-calendar-page',
  imports: [RouterLink, DatePipe],
  templateUrl: './booking-calendar.page.html',
  host: { class: 'page-shell' },
})
export class BookingCalendarPage implements OnInit {
  private readonly bookingService = inject(BookingService);

  readonly bookings = signal<Booking[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly monthCursor = signal(new Date(new Date().getFullYear(), new Date().getMonth(), 1));
  readonly selectedDate = signal<string>(toIsoDate(new Date()));
  readonly formatRange = formatBookingRange;
  readonly statusMeta = bookingStatusMeta;

  readonly monthTitle = computed(() => monthLabel(this.monthCursor()));
  readonly days = computed(() => buildMonthGrid(this.monthCursor(), this.bookings()));
  readonly selectedDayBookings = computed(() => {
    const iso = this.selectedDate();
    return this.bookings().filter((booking) => overlapsDay(booking, iso));
  });
  readonly selectedDateLabel = computed(() =>
    parseIsoDate(this.selectedDate()).toLocaleDateString(undefined, {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    }),
  );

  readonly weekdays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

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

  previousMonth(): void {
    const current = this.monthCursor();
    this.monthCursor.set(new Date(current.getFullYear(), current.getMonth() - 1, 1));
  }

  nextMonth(): void {
    const current = this.monthCursor();
    this.monthCursor.set(new Date(current.getFullYear(), current.getMonth() + 1, 1));
  }

  goToToday(): void {
    const now = new Date();
    this.monthCursor.set(new Date(now.getFullYear(), now.getMonth(), 1));
    this.selectedDate.set(toIsoDate(now));
  }

  selectDay(day: CalendarDay): void {
    this.selectedDate.set(day.isoDate);
  }
}
