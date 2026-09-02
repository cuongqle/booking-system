export type BookingStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';

export interface Booking {
  id: number;
  userId: number;
  roomId: string;
  startDate: string;
  endDate: string;
  status: BookingStatus;
  createdAt: string;
  updatedAt: string;
}

export interface Room {
  id: string;
  name: string;
  description: string;
}

export interface BookingWriteRequest {
  roomId: string;
  startDate: string;
  endDate: string;
}

export interface BookingUpdateRequest extends BookingWriteRequest {
  status: BookingStatus;
}

export const BOOKING_STATUSES: {
  value: BookingStatus;
  label: string;
  chipClass: string;
  pillClass: string;
}[] = [
  {
    value: 'PENDING',
    label: 'Pending',
    chipClass: 'status-chip status-chip--pending',
    pillClass: 'booking-pill booking-pill--pending',
  },
  {
    value: 'CONFIRMED',
    label: 'Confirmed',
    chipClass: 'status-chip status-chip--confirmed',
    pillClass: 'booking-pill booking-pill--confirmed',
  },
  {
    value: 'CANCELLED',
    label: 'Canceled',
    chipClass: 'status-chip status-chip--cancelled',
    pillClass: 'booking-pill booking-pill--cancelled',
  },
  {
    value: 'COMPLETED',
    label: 'Completed',
    chipClass: 'status-chip status-chip--completed',
    pillClass: 'booking-pill booking-pill--completed',
  },
];

export function bookingStatusMeta(status: BookingStatus) {
  return (
    BOOKING_STATUSES.find((item) => item.value === status) ?? {
      value: status,
      label: status,
      chipClass: 'status-chip',
      pillClass: 'booking-pill booking-pill--pending',
    }
  );
}
