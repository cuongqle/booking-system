import { AbstractControl } from '@angular/forms';

export function showControlError(control: AbstractControl | null): boolean {
  return !!control && control.invalid && (control.touched || control.dirty);
}

export function controlErrorMessage(
  control: AbstractControl | null,
  labels: {
    required?: string;
    email?: string;
    minlength?: string;
    min?: string;
    pattern?: string;
  } = {},
): string | null {
  if (!control || !showControlError(control)) {
    return null;
  }

  if (control.hasError('required')) {
    return labels.required ?? 'This field is required';
  }
  if (control.hasError('email')) {
    return labels.email ?? 'Enter a valid email address';
  }
  if (control.hasError('minlength')) {
    const requiredLength = control.getError('minlength')?.requiredLength as number | undefined;
    return labels.minlength ?? `Must be at least ${requiredLength ?? 8} characters`;
  }
  if (control.hasError('min')) {
    const min = control.getError('min')?.min as number | undefined;
    return labels.min ?? `Must be at least ${min ?? 0}`;
  }
  if (control.hasError('pattern')) {
    return labels.pattern ?? 'Enter a valid value';
  }
  return 'Invalid value';
}
