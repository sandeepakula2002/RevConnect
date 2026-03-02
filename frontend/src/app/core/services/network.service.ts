import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  Connection,
  Follow,
  User,
  PageResponse
} from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class NetworkService {

  private readonly API = `${environment.apiUrl}/network`;

  constructor(private http: HttpClient) {}

  // ================= CONNECTIONS =================
  getConnections(): Observable<ApiResponse<Connection[]>> {
    return this.http.get<ApiResponse<Connection[]>>(
      `${this.API}/connections`
    );
  }

  getPendingRequests(): Observable<ApiResponse<Connection[]>> {
    return this.http.get<ApiResponse<Connection[]>>(
      `${this.API}/requests/received`
    );
  }

  getSentRequests(): Observable<ApiResponse<Connection[]>> {
    return this.http.get<ApiResponse<Connection[]>>(
      `${this.API}/requests/sent`
    );
  }

  sendRequest(userId: number): Observable<ApiResponse<Connection>> {
    return this.http.post<ApiResponse<Connection>>(
      `${this.API}/connect/${userId}`,
      {}
    );
  }

  acceptRequest(connectionId: number): Observable<ApiResponse<Connection>> {
    return this.http.put<ApiResponse<Connection>>(
      `${this.API}/connections/${connectionId}/accept`,
      {}
    );
  }

  rejectRequest(connectionId: number): Observable<ApiResponse<Connection>> {
    return this.http.put<ApiResponse<Connection>>(
      `${this.API}/connections/${connectionId}/reject`,
      {}
    );
  }

  removeConnection(userId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(
      `${this.API}/connect/${userId}`
    );
  }

  // ================= FOLLOW =================
  follow(userId: number): Observable<ApiResponse<Follow>> {
    return this.http.post<ApiResponse<Follow>>(
      `${this.API}/follow/${userId}`,
      {}
    );
  }

  unfollow(userId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(
      `${this.API}/follow/${userId}`
    );
  }

  getFollowers(userId: number): Observable<ApiResponse<Follow[]>> {
    return this.http.get<ApiResponse<Follow[]>>(
      `${this.API}/followers/${userId}`
    );
  }

  getFollowing(userId: number): Observable<ApiResponse<Follow[]>> {
    return this.http.get<ApiResponse<Follow[]>>(
      `${this.API}/following/${userId}`
    );
  }

  isFollowing(userId: number): Observable<boolean> {
    return this.http.get<boolean>(
      `${this.API}/is-following/${userId}`
    );
  }

  getFollowerCount(userId: number): Observable<number> {
    return this.http.get<number>(
      `${this.API}/follower-count/${userId}`
    );
  }

  getFollowingCount(userId: number): Observable<number> {
    return this.http.get<number>(
      `${this.API}/following-count/${userId}`
    );
  }

  // ================= DISCOVERY =================
  getSuggestedConnections(limit: number = 10): Observable<User[]> {
    return this.http.get<User[]>(
      `${this.API}/suggestions?limit=${limit}`
    );
  }

  getMutualConnections(userId: number): Observable<User[]> {
    return this.http.get<User[]>(
      `${this.API}/mutual-connections/${userId}`
    );
  }

  searchUsers(
    query: string,
    page: number = 0,
    size: number = 20
  ): Observable<PageResponse<User>> {

    return this.http.get<PageResponse<User>>(
      `${this.API}/search?q=${query}&page=${page}&size=${size}`
    );
  }

  getPeopleYouMayKnow(limit: number = 10): Observable<User[]> {
    return this.http.get<User[]>(
      `${this.API}/people-you-may-know?limit=${limit}`
    );
  }
}
