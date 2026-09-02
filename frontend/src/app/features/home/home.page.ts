import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-home-page',
  imports: [RouterLink],
  templateUrl: './home.page.html',
  host: { class: 'page-shell page-shell--center' },
})
export class HomePage {
  readonly auth = inject(AuthService);

  readonly previewDays = [
    { label: 'S', date: 8, active: false },
    { label: 'M', date: 9, active: false },
    { label: 'T', date: 10, active: true },
    { label: 'W', date: 11, active: true },
    { label: 'T', date: 12, active: true },
    { label: 'F', date: 13, active: false },
    { label: 'S', date: 14, active: false },
  ];
}
