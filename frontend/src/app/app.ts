import { Component, OnDestroy, effect, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
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
