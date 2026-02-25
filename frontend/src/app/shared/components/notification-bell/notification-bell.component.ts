import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-notification-bell',
  templateUrl: './notification-bell.component.html',
  styleUrls: ['./notification-bell.component.css']
})
export class NotificationBellComponent implements OnInit {

  unreadCount: number = 0;
  showDropdown: boolean = false;
  userId: number = 1;

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.loadUnreadCount();
  }

  loadUnreadCount(): void {
    this.notificationService.getUnreadCount(this.userId).subscribe(count => {
      this.unreadCount = count;
    });
  }

  toggleDropdown(): void {
    this.showDropdown = !this.showDropdown;
  }
}
