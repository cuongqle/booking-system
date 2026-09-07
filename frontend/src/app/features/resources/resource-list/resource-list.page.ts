import { Component, OnInit, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AdminResourceService } from '../admin-resource.service';
import { RESOURCE_TYPE_LABELS, Resource } from '../../bookings/booking.models';
import { extractErrorMessage } from '../../../core/api/extract-error-message';

@Component({
  selector: 'app-resource-list-page',
  imports: [RouterLink, DecimalPipe],
  templateUrl: './resource-list.page.html',
  host: { class: 'page-shell' },
})
export class ResourceListPage implements OnInit {
  private readonly adminResources = inject(AdminResourceService);

  readonly resources = signal<Resource[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly typeLabel = RESOURCE_TYPE_LABELS;

  ngOnInit(): void {
    this.adminResources.getResources().subscribe({
      next: (resources) => {
        this.resources.set(resources);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(extractErrorMessage(err, 'Failed to load resources'));
        this.loading.set(false);
      },
    });
  }
}
