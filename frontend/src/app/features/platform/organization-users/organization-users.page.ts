import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PlatformOrganization, PlatformOrganizationService } from '../platform-organization.service';
import { PlatformUser, PlatformUserService } from '../platform-user.service';
import { extractErrorMessage } from '../../../core/api/extract-error-message';

@Component({
  selector: 'app-organization-users-page',
  imports: [RouterLink, DatePipe, FormsModule],
  templateUrl: './organization-users.page.html',
  host: { class: 'page-shell' },
})
export class OrganizationUsersPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly organizationsApi = inject(PlatformOrganizationService);
  private readonly usersApi = inject(PlatformUserService);

  readonly organization = signal<PlatformOrganization | null>(null);
  readonly users = signal<PlatformUser[]>([]);
  readonly loading = signal(true);
  readonly actingId = signal<number | null>(null);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isFinite(id)) {
      this.error.set('Invalid organization');
      this.loading.set(false);
      return;
    }
    this.load(id);
  }

  load(organizationId: number): void {
    this.loading.set(true);
    this.error.set(null);
    this.organizationsApi.get(organizationId).subscribe({
      next: (org) => {
        this.organization.set(org);
        this.usersApi.listByOrganization(organizationId).subscribe({
          next: (users) => {
            this.users.set(users);
            this.loading.set(false);
          },
          error: (err) => {
            this.error.set(extractErrorMessage(err, 'Failed to load users'));
            this.loading.set(false);
          },
        });
      },
      error: (err) => {
        this.error.set(extractErrorMessage(err, 'Failed to load organization'));
        this.loading.set(false);
      },
    });
  }

  setRole(user: PlatformUser, role: 'USER' | 'ADMIN'): void {
    if (user.role === role) {
      return;
    }
    this.patch(user.id, { role });
  }

  setActive(user: PlatformUser, active: boolean): void {
    if (user.active === active) {
      return;
    }
    const action = active ? 'Reactivate' : 'Deactivate';
    if (!window.confirm(`${action} ${user.fullName}?`)) {
      return;
    }
    this.patch(user.id, { active });
  }

  private patch(userId: number, payload: { role?: 'USER' | 'ADMIN'; active?: boolean }): void {
    this.actingId.set(userId);
    this.error.set(null);
    this.usersApi.update(userId, payload).subscribe({
      next: (updated) => {
        this.users.update((list) => list.map((item) => (item.id === updated.id ? updated : item)));
        this.actingId.set(null);
      },
      error: (err) => {
        this.error.set(extractErrorMessage(err, 'Failed to update user'));
        this.actingId.set(null);
      },
    });
  }
}
