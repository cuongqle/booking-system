import { DatePipe } from '@angular/common';
import { Component, HostListener, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { NotificationService } from './notification.service';
import { AppNotification } from './notification.models';

@Component({
  selector: 'app-notification-bell',
  imports: [DatePipe, RouterLink],
  templateUrl: './notification-bell.component.html',
})
export class NotificationBellComponent {
  private readonly router = inject(Router);
  readonly notifications = inject(NotificationService);

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement | null;
    if (!target?.closest('app-notification-bell')) {
      this.notifications.closePanel();
    }
  }

  toggle(event: MouseEvent): void {
    event.stopPropagation();
    this.notifications.togglePanel();
  }

  markAll(event: MouseEvent): void {
    event.stopPropagation();
    this.notifications.markAllRead();
  }

  openItem(notification: AppNotification, event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.notifications.markRead(notification.id).subscribe({
      next: () => {
        this.notifications.closePanel();
        if (notification.link) {
          void this.router.navigateByUrl(notification.link);
        }
      },
      error: () => {
        this.notifications.closePanel();
        if (notification.link) {
          void this.router.navigateByUrl(notification.link);
        }
      },
    });
  }

  isUnread(notification: AppNotification): boolean {
    return !notification.read && !notification.readAt;
  }
}
