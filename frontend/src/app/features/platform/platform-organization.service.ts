import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiUrl } from '../../core/api/api-url';

export interface PlatformOrganization {
  id: number;
  name: string;
  slug: string;
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
}
