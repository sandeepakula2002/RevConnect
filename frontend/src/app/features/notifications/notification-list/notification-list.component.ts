import { Component, OnDestroy, OnInit } from '@angular/core';
import { NotificationService } from '../../../core/services/notification.service';
import { Notification } from '../../../shared/models/models';
import { Router } from '@angular/router';
import { Subject, interval } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-notification-list',
  templateUrl: './notification-list.component.html',
  styleUrls: ['./notification-list.component.css']
})
export class NotificationListComponent implements OnInit, OnDestroy {

  notifications: Notification[] = [];
  loading: boolean = true;
  private destroy$ = new Subject<void>();

  constructor(
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadNotifications();

    // Auto refresh every 10 sec
    interval(10000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.loadNotifications();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // ================= LOAD =================
  loadNotifications(): void {

    this.loading = true;

    this.notificationService.getNotifications()
      .subscribe({
        next: response => {
          this.notifications = response?.data ?? [];
          this.loading = false;
        },
        error: () => {
          this.notifications = [];
          this.loading = false;
        }
      });
  }

  // ================= MARK ONE =================
  markRead(notification: Notification): void {

    if (!notification.read) {
      this.notificationService.markAsRead(notification.id).subscribe({
        next: () => {
          notification.read = true;
        },
        error: () => {
          // Keep current UI state when backend update fails.
        }
      });
    }

    if (
      notification.type === 'POST_LIKED' ||
      notification.type === 'POST_COMMENTED' ||
      notification.type === 'POST_SHARED'
    ) {
      if (notification.referenceId) {
        this.router.navigate(['/post', notification.referenceId])
          .then(() => window.scrollTo(0, 0));
      }
    }
  }

  // ================= MARK ALL =================
  markAllRead(): void {

    this.notificationService.markAllAsRead()
      .subscribe({
        next: () => {
          this.notifications.forEach(n => n.read = true);
        },
        error: () => {
          // Ignore and keep current list state.
        }
      });
  }

  // ================= DELETE =================
  deleteNotif(notification: Notification, index: number, event: Event): void {

    event.stopPropagation();

    this.notificationService.deleteNotification(notification.id).subscribe({
      next: () => {
        this.notifications.splice(index, 1);
      },
      error: () => {
        // Keep item if delete fails.
      }
    });
  }

  // ================= UNREAD COUNT =================
  getUnreadCount(): number {
    return (this.notifications ?? []).filter(n => !n.read).length;
  }

  // ================= ICON =================
  getIcon(type: string): string {

    switch (type) {
      case 'NEW_FOLLOWER': return '👤';
      case 'POST_LIKED': return '❤️';
      case 'POST_COMMENTED': return '💬';
      case 'CONNECTION_REQUEST': return '🔗';
      default: return '🔔';
    }
  }

  // ================= ICON BACKGROUND =================
  getIconBg(type: string): string {

    switch (type) {
      case 'NEW_FOLLOWER': return 'bg-primary';
      case 'POST_LIKED': return 'bg-danger';
      case 'POST_COMMENTED': return 'bg-success';
      case 'CONNECTION_REQUEST': return 'bg-warning';
      default: return 'bg-secondary';
    }
  }

  // ================= MESSAGE FORMAT =================
  getMessage(notification: Notification): string {
    return notification.message || 'You have a new notification';
  }
}
