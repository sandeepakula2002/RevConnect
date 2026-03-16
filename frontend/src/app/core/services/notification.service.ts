import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, EMPTY, interval, Observable, Subscription } from 'rxjs';
import { catchError, startWith, switchMap, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ApiResponse, Notification } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class NotificationService {

  private readonly API = `${environment.apiUrl}/notifications`;
  private unreadCount = new BehaviorSubject<number>(0);
  private pollingSubscription: Subscription | null = null;
  unreadCount$ = this.unreadCount.asObservable();

  constructor(private http: HttpClient) {}

  getNotifications(page = 0, size = 20): Observable<ApiResponse<Notification[]>> {
    return this.http.get<ApiResponse<Notification[]>>(
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

  deleteNotification(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API}/${id}`);
  }

  // Poll for new notifications every 30 seconds
  startPolling(): void {
    if (this.pollingSubscription && !this.pollingSubscription.closed) {
      return;
    }

    this.pollingSubscription = interval(30000).pipe(
      startWith(0),
      switchMap(() => this.getUnreadCount().pipe(
        catchError((error: HttpErrorResponse) => {
          // These are expected while unauthenticated or when backend/dev server is down.
          if (error.status === 0 || error.status === 401 || error.status === 403) {
            this.unreadCount.next(0);
            return EMPTY;
          }
          return EMPTY;
        })
      ))
    ).subscribe();
  }

  stopPolling(): void {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
      this.pollingSubscription = null;
    }
    this.unreadCount.next(0);
  }
}
