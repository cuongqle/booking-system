import { Component, OnDestroy, computed, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter, map } from 'rxjs';
import { AuthService } from './core/auth/auth.service';
import { NotificationBellComponent } from './features/notifications/notification-bell.component';
import { NotificationService } from './features/notifications/notification.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, NotificationBellComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnDestroy {
  private readonly router = inject(Router);
  private readonly notifications = inject(NotificationService);
  readonly auth = inject(AuthService);
  readonly year = new Date().getFullYear();

  private readonly currentUrl = toSignal(
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd),
      map((event) => event.urlAfterRedirects),
    ),
    { initialValue: this.router.url },
  );

  readonly isLanding = computed(() => {
    const path = (this.currentUrl() ?? '/').split('?')[0];
    return path === '/' || path === '';
  });

  private pollId: ReturnType<typeof setInterval> | null = null;

  constructor() {
    effect(() => {
      if (this.auth.isAuthenticated()) {
        this.notifications.refresh();
        this.startPolling();
      } else {
        this.stopPolling();
        this.notifications.clear();
      }
    });
  }

  ngOnDestroy(): void {
    this.stopPolling();
  }

  logout(): void {
    this.stopPolling();
    this.notifications.clear();
    this.auth.logout();
    void this.router.navigateByUrl('/');
  }

  private startPolling(): void {
    if (this.pollId != null) {
      return;
    }
    this.pollId = setInterval(() => {
      if (this.auth.isAuthenticated()) {
        this.notifications.refresh();
      }
    }, 30_000);
  }

  private stopPolling(): void {
    if (this.pollId != null) {
      clearInterval(this.pollId);
      this.pollId = null;
    }
  }
}
