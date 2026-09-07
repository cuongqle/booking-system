import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiUrl } from '../../core/api/api-url';
import { UserRole } from '../../core/auth/auth.models';

export interface PlatformUser {
  id: number;
  organizationId: number;
  email: string;
  fullName: string;
  role: UserRole;
  active: boolean;
  createdAt: string;
}

export interface UpdatePlatformUserRequest {
  role?: 'USER' | 'ADMIN';
  active?: boolean;
}

@Injectable({ providedIn: 'root' })
export class PlatformUserService {
  private readonly http = inject(HttpClient);

  listByOrganization(organizationId: number): Observable<PlatformUser[]> {
    return this.http.get<PlatformUser[]>(
      apiUrl(`/platform/organizations/${organizationId}/users`),
    );
  }

  update(userId: number, payload: UpdatePlatformUserRequest): Observable<PlatformUser> {
    return this.http.patch<PlatformUser>(apiUrl(`/platform/users/${userId}`), payload);
  }
}
