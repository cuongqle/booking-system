import { Booking } from './booking.models';

export interface CalendarDay {
  date: Date;
  isoDate: string;
  inCurrentMonth: boolean;
  isToday: boolean;
  bookings: Booking[];
}

export function toIsoDate(date: Date): string {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

export function parseIsoDate(value: string): Date {
  const [year, month, day] = value.split('-').map(Number);
  return new Date(year, month - 1, day);
}

export function bookingDatePart(value: string): string {
  return value.slice(0, 10);
}

export function overlapsDay(booking: Booking, isoDate: string): boolean {
  return bookingDatePart(booking.startDate) <= isoDate && bookingDatePart(booking.endDate) >= isoDate;
}

export function buildMonthGrid(month: Date, bookings: Booking[]): CalendarDay[] {
  const year = month.getFullYear();
  const monthIndex = month.getMonth();
  const firstOfMonth = new Date(year, monthIndex, 1);
  const startOffset = firstOfMonth.getDay(); // Sunday = 0
  const gridStart = new Date(year, monthIndex, 1 - startOffset);
  const todayIso = toIsoDate(new Date());

  const days: CalendarDay[] = [];
  for (let i = 0; i < 42; i++) {
    const date = new Date(gridStart);
    date.setDate(gridStart.getDate() + i);
    const isoDate = toIsoDate(date);
    days.push({
      date,
      isoDate,
      inCurrentMonth: date.getMonth() === monthIndex,
      isToday: isoDate === todayIso,
      bookings: bookings.filter((booking) => overlapsDay(booking, isoDate)),
    });
  }
  return days;
}

export function monthLabel(month: Date): string {
  return month.toLocaleDateString(undefined, { month: 'long', year: 'numeric' });
}

export function formatBookingRange(startDate: string, endDate: string): string {
  const start = new Date(startDate);
  const end = new Date(endDate);
  const opts: Intl.DateTimeFormatOptions = {
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  };
  return `${start.toLocaleString(undefined, opts)} → ${end.toLocaleString(undefined, opts)}`;
}
