import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../../../core/services/notification.service';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { Notification } from '../../../shared/models/models';

@Component({
  selector: 'app-notification-list',
  template: `
    <div class="container py-4" style="max-width:700px;">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h4 class="fw-bold mb-0">🔔 Notifications</h4>
        <button class="btn btn-outline-secondary btn-sm" (click)="markAllRead()">
          Mark all as read
        </button>
      </div>

      <div class="text-center py-5" *ngIf="loading">
        <div class="spinner-border text-primary"></div>
      </div>

      <div class="text-center py-5 text-muted" *ngIf="!loading && notifications.length === 0">
        No notifications yet.
      </div>

      <div class="list-group shadow-sm">
        <div *ngFor="let n of notifications"
             class="list-group-item list-group-item-action d-flex gap-3 py-3"
             [class.list-group-item-light]="!n.read"
             (click)="markRead(n)">
          <div class="rounded-circle bg-primary text-white d-flex align-items-center
                      justify-content-center flex-shrink-0" style="width:42px;height:42px;">
            {{ getIcon(n.type) }}
          </div>
          <div class="flex-grow-1">
            <p class="mb-1">{{ n.message }}</p>
            <small class="text-muted">{{ n.createdAt | date:'short' }}</small>
          </div>
          <span *ngIf="!n.read" class="badge bg-primary rounded-pill align-self-center">New</span>
        </div>
      </div>
    </div>
  `
})
export class NotificationListComponent implements OnInit {

  notifications: Notification[] = [];
  loading = true;

  constructor(private notificationService: NotificationService) {}

  ngOnInit() {
    this.notificationService.getNotifications().subscribe({
      next: (res) => { this.notifications = res.data.content; this.loading = false; },
      error: () => this.loading = false
    });
  }

  markRead(n: Notification) {
    if (!n.read) {
      this.notificationService.markAsRead(n.id).subscribe(() => n.read = true);
    }
  }

  markAllRead() {
    this.notificationService.markAllAsRead().subscribe(() => {
      this.notifications.forEach(n => n.read = true);
    });
  }

  getIcon(type: string): string {
    const icons: Record<string, string> = {
      CONNECTION_REQUEST: '🤝', CONNECTION_ACCEPTED: '✅',
      NEW_FOLLOWER: '👤', POST_LIKED: '❤️',
      POST_COMMENTED: '💬', POST_SHARED: '🔄'
    };
    return icons[type] || '🔔';
  }
}
