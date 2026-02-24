import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, Connection, Follow } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class NetworkService {

  private readonly API = `${environment.apiUrl}/network`;

  constructor(private http: HttpClient) {}

  // ─── Connections ─────────────────────────────────────────────────
  sendRequest(userId: number): Observable<ApiResponse<Connection>> {
    return this.http.post<ApiResponse<Connection>>(`${this.API}/connect/${userId}`, {});
  }

  acceptRequest(connectionId: number): Observable<ApiResponse<Connection>> {
    return this.http.put<ApiResponse<Connection>>(
      `${this.API}/connections/${connectionId}/accept`, {});
  }

  rejectRequest(connectionId: number): Observable<ApiResponse<Connection>> {
    return this.http.put<ApiResponse<Connection>>(
      `${this.API}/connections/${connectionId}/reject`, {});
  }

  removeConnection(userId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API}/connect/${userId}`);
  }

  getConnections(): Observable<ApiResponse<Connection[]>> {
    return this.http.get<ApiResponse<Connection[]>>(`${this.API}/connections`);
  }

  getPendingRequests(): Observable<ApiResponse<Connection[]>> {
    return this.http.get<ApiResponse<Connection[]>>(`${this.API}/requests/received`);
  }

  getSentRequests(): Observable<ApiResponse<Connection[]>> {
    return this.http.get<ApiResponse<Connection[]>>(`${this.API}/requests/sent`);
  }

  // ─── Follow ──────────────────────────────────────────────────────
  follow(userId: number): Observable<ApiResponse<Follow>> {
    return this.http.post<ApiResponse<Follow>>(`${this.API}/follow/${userId}`, {});
  }

  unfollow(userId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API}/follow/${userId}`);
  }

  getFollowers(userId: number): Observable<ApiResponse<Follow[]>> {
    return this.http.get<ApiResponse<Follow[]>>(`${this.API}/followers/${userId}`);
  }

  getFollowing(userId: number): Observable<ApiResponse<Follow[]>> {
    return this.http.get<ApiResponse<Follow[]>>(`${this.API}/following/${userId}`);
  }
}
