import { AbstractControl } from '@angular/forms';

export function showControlError(control: AbstractControl | null): boolean {
  return !!control && control.invalid && (control.touched || control.dirty);
}

export function controlErrorMessage(
  control: AbstractControl | null,
  labels: { required?: string; email?: string; minlength?: string } = {},
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
  return 'Invalid value';
}
