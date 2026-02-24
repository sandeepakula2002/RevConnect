import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, interval, Observable } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ApiResponse, Notification, PageResponse } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class NotificationService {

  private readonly API = `${environment.apiUrl}/notifications`;
  private unreadCount = new BehaviorSubject<number>(0);
  unreadCount$ = this.unreadCount.asObservable();

  constructor(private http: HttpClient) {}

  getNotifications(page = 0, size = 20): Observable<ApiResponse<PageResponse<Notification>>> {
    return this.http.get<ApiResponse<PageResponse<Notification>>>(
      `${this.API}?page=${page}&size=${size}`
    );
  }

  getUnreadCount(): Observable<ApiResponse<{ count: number }>> {
    return this.http.get<ApiResponse<{ count: number }>>(`${this.API}/unread-count`).pipe(
      tap(res => this.unreadCount.next(res.data.count))
    );
  }

  markAsRead(id: number): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.API}/${id}/read`, {});
  }

  markAllAsRead(): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.API}/read-all`, {});
  }

  // Poll for new notifications every 30 seconds
  startPolling(): void {
    interval(30000).pipe(
      switchMap(() => this.getUnreadCount())
    ).subscribe();
  }
}
