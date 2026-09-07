import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiUrl } from '../../core/api/api-url';
import { Resource, ResourceBlackout, ResourceType } from '../bookings/booking.models';

export interface ResourceWriteRequest {
  id?: string;
  name: string;
  description: string | null;
  type: ResourceType;
  active: boolean;
  pricePerHour: number;
  currency: string;
  minDurationMinutes: number;
  maxDurationMinutes: number | null;
  bufferMinutes: number;
  openTime: string | null;
  closeTime: string | null;
}

export interface BlackoutWriteRequest {
  startAt: string;
  endAt: string;
  reason: string | null;
}

@Injectable({ providedIn: 'root' })
export class AdminResourceService {
  private readonly http = inject(HttpClient);

  getResources(): Observable<Resource[]> {
    return this.http.get<Resource[]>(apiUrl('/admin/resources'));
  }

  getResource(id: string): Observable<Resource> {
    return this.http.get<Resource>(apiUrl(`/admin/resources/${id}`));
  }

  createResource(payload: ResourceWriteRequest): Observable<Resource> {
    return this.http.post<Resource>(apiUrl('/admin/resources'), payload);
  }

  updateResource(id: string, payload: Omit<ResourceWriteRequest, 'id'>): Observable<Resource> {
    return this.http.put<Resource>(apiUrl(`/admin/resources/${id}`), payload);
  }

  listBlackouts(resourceId: string): Observable<ResourceBlackout[]> {
    return this.http.get<ResourceBlackout[]>(apiUrl(`/admin/resources/${resourceId}/blackouts`));
  }

  createBlackout(resourceId: string, payload: BlackoutWriteRequest): Observable<ResourceBlackout> {
    return this.http.post<ResourceBlackout>(
      apiUrl(`/admin/resources/${resourceId}/blackouts`),
      payload,
    );
  }

  deleteBlackout(resourceId: string, blackoutId: number): Observable<void> {
    return this.http.delete<void>(
      apiUrl(`/admin/resources/${resourceId}/blackouts/${blackoutId}`),
    );
  }
}
