import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { AdminResourceService } from '../admin-resource.service';
import {
  RESOURCE_TYPE_LABELS,
  ResourceBlackout,
  ResourceType,
} from '../../bookings/booking.models';
import { extractErrorMessage } from '../../../core/api/extract-error-message';
import { controlErrorMessage, showControlError } from '../../../core/forms/form-errors';

@Component({
  selector: 'app-resource-form-page',
  imports: [ReactiveFormsModule, RouterLink, DatePipe],
  templateUrl: './resource-form.page.html',
  host: { class: 'page-shell' },
})
export class ResourceFormPage implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly adminResources = inject(AdminResourceService);

  readonly resourceTypes = Object.entries(RESOURCE_TYPE_LABELS).map(([value, label]) => ({
    value: value as ResourceType,
    label,
  }));
  readonly editingId = signal<string | null>(null);
  readonly loading = signal(false);
  readonly submitting = signal(false);
  readonly blackoutSubmitting = signal(false);
  readonly error = signal<string | null>(null);
  readonly blackoutError = signal<string | null>(null);
  readonly blackouts = signal<ResourceBlackout[]>([]);
  readonly showError = showControlError;
  readonly errorMessage = controlErrorMessage;

  readonly form = this.fb.nonNullable.group({
    id: ['', [Validators.required, Validators.maxLength(64)]],
    name: ['', [Validators.required, Validators.maxLength(255)]],
    description: [''],
    type: ['MEETING_ROOM' as ResourceType, [Validators.required]],
    active: [true, [Validators.required]],
    pricePerHour: [0, [Validators.required, Validators.min(0)]],
    currency: ['USD', [Validators.required, Validators.pattern(/^[A-Z]{3}$/)]],
    minDurationMinutes: [30, [Validators.required, Validators.min(1)]],
    maxDurationMinutes: [null as number | null, [Validators.min(1)]],
    bufferMinutes: [0, [Validators.required, Validators.min(0)]],
    openTime: [''],
    closeTime: [''],
  });

  readonly blackoutForm = this.fb.nonNullable.group({
    startAt: ['', Validators.required],
    endAt: ['', Validators.required],
    reason: [''],
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      return;
    }

    this.editingId.set(id);
    this.form.controls.id.disable();
    this.loading.set(true);

    this.adminResources.getResource(id).subscribe({
      next: (resource) => {
        this.form.patchValue({
          id: resource.id,
          name: resource.name,
          description: resource.description ?? '',
          type: resource.type,
          active: resource.active,
          pricePerHour: resource.pricePerHour,
          currency: resource.currency,
          minDurationMinutes: resource.minDurationMinutes,
          maxDurationMinutes: resource.maxDurationMinutes,
          bufferMinutes: resource.bufferMinutes,
          openTime: this.toTimeInput(resource.openTime),
          closeTime: this.toTimeInput(resource.closeTime),
        });
        this.loadBlackouts(id);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(extractErrorMessage(err, 'Failed to load resource'));
        this.loading.set(false);
      },
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Please fix the highlighted fields');
      return;
    }

    const raw = this.form.getRawValue();
    if (
      raw.maxDurationMinutes != null &&
      raw.maxDurationMinutes < raw.minDurationMinutes
    ) {
      this.error.set('Max duration must be greater than or equal to min duration');
      return;
    }

    const openTime = this.fromTimeInput(raw.openTime);
    const closeTime = this.fromTimeInput(raw.closeTime);
    if ((openTime && !closeTime) || (!openTime && closeTime)) {
      this.error.set('Set both open and close time, or leave both empty for 24/7');
      return;
    }

    this.submitting.set(true);
    this.error.set(null);

    const payload = {
      name: raw.name,
      description: raw.description.trim() ? raw.description.trim() : null,
      type: raw.type,
      active: raw.active,
      pricePerHour: Number(raw.pricePerHour),
      currency: raw.currency.trim().toUpperCase(),
      minDurationMinutes: Number(raw.minDurationMinutes),
      maxDurationMinutes: raw.maxDurationMinutes == null ? null : Number(raw.maxDurationMinutes),
      bufferMinutes: Number(raw.bufferMinutes),
      openTime,
      closeTime,
    };

    const request$ = this.editingId()
      ? this.adminResources.updateResource(this.editingId()!, payload)
      : this.adminResources.createResource({ ...payload, id: raw.id.trim() });

    request$.subscribe({
      next: () => {
        this.submitting.set(false);
        void this.router.navigate(['/resources']);
      },
      error: (err) => {
        this.submitting.set(false);
        this.error.set(extractErrorMessage(err, 'Failed to save resource'));
      },
    });
  }

  addBlackout(): void {
    const id = this.editingId();
    if (!id || this.blackoutForm.invalid) {
      this.blackoutForm.markAllAsTouched();
      return;
    }
    const raw = this.blackoutForm.getRawValue();
    this.blackoutSubmitting.set(true);
    this.blackoutError.set(null);
    this.adminResources
      .createBlackout(id, {
        startAt: this.fromDateTimeLocal(raw.startAt),
        endAt: this.fromDateTimeLocal(raw.endAt),
        reason: raw.reason.trim() ? raw.reason.trim() : null,
      })
      .subscribe({
        next: () => {
          this.blackoutForm.reset({ startAt: '', endAt: '', reason: '' });
          this.blackoutSubmitting.set(false);
          this.loadBlackouts(id);
        },
        error: (err) => {
          this.blackoutSubmitting.set(false);
          this.blackoutError.set(extractErrorMessage(err, 'Failed to add blackout'));
        },
      });
  }

  removeBlackout(blackoutId: number): void {
    const id = this.editingId();
    if (!id) {
      return;
    }
    this.adminResources.deleteBlackout(id, blackoutId).subscribe({
      next: () => this.loadBlackouts(id),
      error: (err) =>
        this.blackoutError.set(extractErrorMessage(err, 'Failed to remove blackout')),
    });
  }

  private loadBlackouts(id: string): void {
    this.adminResources.listBlackouts(id).subscribe({
      next: (items) => this.blackouts.set(items),
      error: () => this.blackouts.set([]),
    });
  }

  private toTimeInput(value: string | null): string {
    if (!value) {
      return '';
    }
    return value.slice(0, 5);
  }

  private fromTimeInput(value: string): string | null {
    if (!value?.trim()) {
      return null;
    }
    return value.length === 5 ? `${value}:00` : value;
  }

  private fromDateTimeLocal(value: string): string {
    return value.length === 16 ? `${value}:00` : value;
  }
}
