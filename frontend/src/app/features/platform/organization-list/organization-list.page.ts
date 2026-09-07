import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PlatformOrganization, PlatformOrganizationService } from '../platform-organization.service';
import { extractErrorMessage } from '../../../core/api/extract-error-message';

@Component({
  selector: 'app-organization-list-page',
  imports: [FormsModule, DatePipe, RouterLink],
  templateUrl: './organization-list.page.html',
  host: { class: 'page-shell' },
})
export class OrganizationListPage implements OnInit {
  private readonly organizationsApi = inject(PlatformOrganizationService);

  readonly organizations = signal<PlatformOrganization[]>([]);
  readonly loading = signal(true);
  readonly submitting = signal(false);
  readonly actingId = signal<number | null>(null);
  readonly error = signal<string | null>(null);
  readonly newName = signal('');

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.loading.set(true);
    this.error.set(null);
    this.organizationsApi.list().subscribe({
      next: (orgs) => {
        this.organizations.set(orgs);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(extractErrorMessage(err, 'Failed to load organizations'));
        this.loading.set(false);
      },
    });
  }

  create(): void {
    const name = this.newName().trim();
    if (!name) {
      this.error.set('Company name is required');
      return;
    }
    this.submitting.set(true);
    this.error.set(null);
    this.organizationsApi.create(name).subscribe({
      next: (created) => {
        this.organizations.update((list) =>
          [...list, created].sort((a, b) => a.name.localeCompare(b.name)),
        );
        this.newName.set('');
        this.submitting.set(false);
      },
      error: (err) => {
        this.error.set(extractErrorMessage(err, 'Failed to create organization'));
        this.submitting.set(false);
      },
    });
  }

  suspend(org: PlatformOrganization): void {
    const reason = window.prompt(
      `Suspend ${org.name}? Optional reason:`,
      'Suspended by platform admin',
    );
    if (reason === null) {
      return;
    }
    this.actingId.set(org.id);
    this.error.set(null);
    this.organizationsApi.suspend(org.id, reason.trim() || undefined).subscribe({
      next: (updated) => {
        this.replaceOrg(updated);
        this.actingId.set(null);
      },
      error: (err) => {
        this.error.set(extractErrorMessage(err, 'Failed to suspend organization'));
        this.actingId.set(null);
      },
    });
  }

  unsuspend(org: PlatformOrganization): void {
    if (!window.confirm(`Restore access for ${org.name}?`)) {
      return;
    }
    this.actingId.set(org.id);
    this.error.set(null);
    this.organizationsApi.unsuspend(org.id).subscribe({
      next: (updated) => {
        this.replaceOrg(updated);
        this.actingId.set(null);
      },
      error: (err) => {
        this.error.set(extractErrorMessage(err, 'Failed to unsuspend organization'));
        this.actingId.set(null);
      },
    });
  }

  private replaceOrg(updated: PlatformOrganization): void {
    this.organizations.update((list) =>
      list.map((item) => (item.id === updated.id ? updated : item)),
    );
  }
}
