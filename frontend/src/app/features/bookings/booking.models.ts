export type BookingStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';

export type ResourceType = 'MEETING_ROOM' | 'DESK' | 'EQUIPMENT' | 'OTHER';

export interface Booking {
  id: number;
  userId: number;
  resourceId: string;
  startDate: string;
  endDate: string;
  status: BookingStatus;
  totalAmount: number;
  currency: string;
  createdAt: string;
  updatedAt: string;
}

export interface Resource {
  id: string;
  name: string;
  description: string | null;
  type: ResourceType;
  active: boolean;
  pricePerHour: number;
  currency: string;
  minDurationMinutes: number;
  maxDurationMinutes: number | null;
  bufferMinutes: number;
  createdAt: string;
  updatedAt: string;
}

export interface BookingWriteRequest {
  resourceId: string;
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

export const RESOURCE_TYPE_LABELS: Record<ResourceType, string> = {
  MEETING_ROOM: 'Meeting room',
  DESK: 'Desk',
  EQUIPMENT: 'Equipment',
  OTHER: 'Other',
};

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

export function formatMoney(amount: number, currency: string): string {
  try {
    return new Intl.NumberFormat(undefined, {
      style: 'currency',
      currency,
    }).format(amount);
  } catch {
    return `${amount.toFixed(2)} ${currency}`;
  }
}

export function quoteBookingTotal(
  resource: Resource | undefined,
  startLocal: string,
  endLocal: string,
): number | null {
  if (!resource || !startLocal || !endLocal) {
    return null;
  }
  const start = new Date(startLocal);
  const end = new Date(endLocal);
  if (Number.isNaN(start.getTime()) || Number.isNaN(end.getTime()) || end <= start) {
    return null;
  }
  const minutes = (end.getTime() - start.getTime()) / 60_000;
  return Math.round(resource.pricePerHour * (minutes / 60) * 100) / 100;
}
