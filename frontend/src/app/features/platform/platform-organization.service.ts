import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiUrl } from '../../core/api/api-url';

export type OrganizationStatus = 'ACTIVE' | 'SUSPENDED';

export interface PlatformOrganization {
  id: number;
  name: string;
  slug: string;
  status: OrganizationStatus;
  suspendedAt: string | null;
  suspendedReason: string | null;
  userCount: number;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class PlatformOrganizationService {
  private readonly http = inject(HttpClient);

  list(): Observable<PlatformOrganization[]> {
    return this.http.get<PlatformOrganization[]>(apiUrl('/platform/organizations'));
  }

  create(name: string): Observable<PlatformOrganization> {
    return this.http.post<PlatformOrganization>(apiUrl('/platform/organizations'), { name });
  }

  suspend(id: number, reason?: string): Observable<PlatformOrganization> {
    return this.http.post<PlatformOrganization>(apiUrl(`/platform/organizations/${id}/suspend`), {
      reason: reason ?? null,
    });
  }

  unsuspend(id: number): Observable<PlatformOrganization> {
    return this.http.post<PlatformOrganization>(
      apiUrl(`/platform/organizations/${id}/unsuspend`),
      {},
    );
  }

  get(id: number): Observable<PlatformOrganization> {
    return this.http.get<PlatformOrganization>(apiUrl(`/platform/organizations/${id}`));
  }
}
