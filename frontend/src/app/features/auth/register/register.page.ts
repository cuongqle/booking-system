import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { extractErrorMessage } from '../../../core/api/extract-error-message';
import { controlErrorMessage, showControlError } from '../../../core/forms/form-errors';

@Component({
  selector: 'app-register-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.page.html',
  host: { class: 'page-shell page-shell--center' },
})
export class RegisterPage {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly error = signal<string | null>(null);
  readonly submitting = signal(false);
  readonly mode = signal<'create' | 'join'>('create');
  readonly showError = showControlError;
  readonly errorMessage = controlErrorMessage;

  readonly form = this.fb.nonNullable.group({
    fullName: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    organizationName: ['', [Validators.required]],
    organizationSlug: [''],
  });

  setMode(mode: 'create' | 'join'): void {
    this.mode.set(mode);
    if (mode === 'create') {
      this.form.controls.organizationName.setValidators([Validators.required]);
      this.form.controls.organizationSlug.clearValidators();
      this.form.controls.organizationSlug.setValue('');
    } else {
      this.form.controls.organizationSlug.setValidators([Validators.required]);
      this.form.controls.organizationName.clearValidators();
      this.form.controls.organizationName.setValue('');
    }
    this.form.controls.organizationName.updateValueAndValidity();
    this.form.controls.organizationSlug.updateValueAndValidity();
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Please fix the highlighted fields');
      return;
    }

    this.submitting.set(true);
    this.error.set(null);

    const raw = this.form.getRawValue();
    const payload =
      this.mode() === 'create'
        ? {
            fullName: raw.fullName,
            email: raw.email,
            password: raw.password,
            organizationName: raw.organizationName,
          }
        : {
            fullName: raw.fullName,
            email: raw.email,
            password: raw.password,
            organizationSlug: raw.organizationSlug.trim().toLowerCase(),
          };

    this.auth.register(payload).subscribe({
      next: () => {
        this.submitting.set(false);
        void this.router.navigateByUrl('/bookings');
      },
      error: (err) => {
        this.submitting.set(false);
        this.error.set(extractErrorMessage(err, 'Registration failed'));
      },
    });
  }
}
