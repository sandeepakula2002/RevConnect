import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Notification } from '../models/notification';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private apiUrl = 'http://localhost:8080/api/notifications';

  constructor(private http: HttpClient) {}

  getUserNotifications(userId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/user/${userId}`);
  }

  getUnreadCount(userId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/user/${userId}/unread-count`);
  }

  markAsRead(notificationId: number): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/${notificationId}/read`, {});
  }

  markAllAsRead(userId: number): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/user/${userId}/read-all`, {});
  }
}
