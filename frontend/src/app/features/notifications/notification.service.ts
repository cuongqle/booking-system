import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, of, tap } from 'rxjs';
import { apiUrl } from '../../core/api/api-url';
import { AppNotification } from './notification.models';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly http = inject(HttpClient);

  private readonly itemsSignal = signal<AppNotification[]>([]);
  private readonly unreadSignal = signal(0);
  private readonly openSignal = signal(false);
  private readonly loadingSignal = signal(false);

  readonly items = this.itemsSignal.asReadonly();
  readonly unreadCount = this.unreadSignal.asReadonly();
  readonly open = this.openSignal.asReadonly();
  readonly loading = this.loadingSignal.asReadonly();
  readonly hasUnread = computed(() => this.unreadSignal() > 0);

  refresh(): void {
    this.http
      .get<{ count: number }>(apiUrl('/notifications/unread-count'))
      .pipe(catchError(() => of({ count: 0 })))
      .subscribe((response) => this.unreadSignal.set(response.count));
  }

  load(): void {
    this.loadingSignal.set(true);
    this.http
      .get<AppNotification[]>(apiUrl('/notifications'))
      .pipe(catchError(() => of([])))
      .subscribe((items) => {
        this.itemsSignal.set(items);
        this.unreadSignal.set(items.filter((item) => !item.read && !item.readAt).length);
        this.loadingSignal.set(false);
      });
  }

  togglePanel(): void {
    const next = !this.openSignal();
    this.openSignal.set(next);
    if (next) {
      this.load();
    }
  }

  closePanel(): void {
    this.openSignal.set(false);
  }

  markRead(id: number): Observable<AppNotification> {
    return this.http.post<AppNotification>(apiUrl(`/notifications/${id}/read`), {}).pipe(
      tap((updated) => {
        this.itemsSignal.update((items) =>
          items.map((item) => (item.id === id ? { ...item, ...updated, read: true } : item)),
        );
        this.refresh();
      }),
    );
  }

  markAllRead(): void {
    this.http
      .post<{ updated: number }>(apiUrl('/notifications/read-all'), {})
      .subscribe(() => {
        this.itemsSignal.update((items) =>
          items.map((item) => ({ ...item, read: true, readAt: item.readAt ?? new Date().toISOString() })),
        );
        this.unreadSignal.set(0);
      });
  }

  clear(): void {
    this.itemsSignal.set([]);
    this.unreadSignal.set(0);
    this.openSignal.set(false);
  }
}
