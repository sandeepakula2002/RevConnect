import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { NotificationService } from '../../services/notification.service';
import { Notification } from '../../models/notification';

@Component({
  selector: 'app-notification-list',
  templateUrl: './notification-list.component.html',
  styleUrls: ['./notification-list.component.css']
})
export class NotificationListComponent implements OnInit {

  @Input() userId: number = 1;
  @Output() notificationRead = new EventEmitter<void>();

  notifications: Notification[] = [];
  isLoading: boolean = false;

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.isLoading = true;
    this.notificationService.getUserNotifications(this.userId).subscribe({
      next: (data) => {
        this.notifications = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading notifications', err);
        this.isLoading = false;
      }
    });
  }

  markAsRead(notification: Notification): void {
    if (notification.isRead) return;
    this.notificationService.markAsRead(notification.id).subscribe(() => {
      notification.isRead = true;
      this.notificationRead.emit();
    });
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead(this.userId).subscribe(() => {
      this.notifications.forEach(n => n.isRead = true);
      this.notificationRead.emit();
    });
  }

  getIcon(type: string): string {
    switch(type) {
      case 'LIKE':    return '❤️';
      case 'COMMENT': return '💬';
      case 'FOLLOW':  return '👤';
      case 'SHARE':   return '🔁';
      default:        return '🔔';
    }
  }
}
