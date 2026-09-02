import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function dateRangeValidator(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const start = group.get('startDate')?.value as string | null;
    const end = group.get('endDate')?.value as string | null;
    if (!start || !end) {
      return null;
    }
    return start >= end ? { dateRange: true } : null;
  };
}

/** Convert API LocalDateTime (`2026-09-10T14:00:00`) to datetime-local input value. */
export function toDatetimeLocalValue(iso: string): string {
  return iso.length >= 16 ? iso.slice(0, 16) : iso;
}

/** Convert datetime-local value to API LocalDateTime string. */
export function fromDatetimeLocalValue(value: string): string {
  if (!value) {
    return value;
  }
  return value.length === 16 ? `${value}:00` : value;
}
