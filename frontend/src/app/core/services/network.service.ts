import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, Connection, Follow, User, PageResponse } from '../../shared/models/models';

@Injectable({
  providedIn: 'root'
})
export class NetworkService {
  private apiUrl = '/api/network';

  constructor(private http: HttpClient) {}

  // ========== Connection Methods ==========

  /**
   * Get all accepted connections for the current user
   */
  getConnections(): Observable<ApiResponse<Connection[]>> {
    return this.http.get<ApiResponse<Connection[]>>(`${this.apiUrl}/connections`);
  }

  /**
   * Get all pending connection requests received by the current user
   */
  getPendingRequests(): Observable<ApiResponse<Connection[]>> {
    return this.http.get<ApiResponse<Connection[]>>(`${this.apiUrl}/requests/received`);
  }

  /**
   * Get all pending connection requests sent by the current user
   */
  getSentRequests(): Observable<ApiResponse<Connection[]>> {
    return this.http.get<ApiResponse<Connection[]>>(`${this.apiUrl}/requests/sent`);
  }

  /**
   * Send a connection request to another user
   * @param userId The ID of the user to connect with
   */
  sendRequest(userId: number): Observable<ApiResponse<Connection>> {
    return this.http.post<ApiResponse<Connection>>(`${this.apiUrl}/connect/${userId}`, {});
  }

  /**
   * Accept a pending connection request
   * @param connectionId The ID of the connection request to accept
   */
  acceptRequest(connectionId: number): Observable<ApiResponse<Connection>> {
    return this.http.put<ApiResponse<Connection>>(`${this.apiUrl}/connections/${connectionId}/accept`, {});
  }

  /**
   * Reject a pending connection request
   * @param connectionId The ID of the connection request to reject
   */
  rejectRequest(connectionId: number): Observable<ApiResponse<Connection>> {
    return this.http.put<ApiResponse<Connection>>(`${this.apiUrl}/connections/${connectionId}/reject`, {});
  }

  /**
   * Remove an existing connection
   * @param userId The ID of the user to disconnect from
   */
  removeConnection(userId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/connect/${userId}`);
  }

  /**
   * Check if two users are connected
   * @param userId1 First user ID
   * @param userId2 Second user ID
   */
  areConnected(userId1: number, userId2: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/are-connected/${userId1}/${userId2}`);
  }

  /**
   * Get connection count for a user
   * @param userId The ID of the user
   */
  getConnectionCount(userId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/connection-count/${userId}`);
  }

  // ========== Follow Methods ==========

  /**
   * Follow a user - matches your controller's POST /follow/{userId}
   * @param userId The ID of the user to follow
   */
  followUser(userId: number): Observable<ApiResponse<Follow>> {
    return this.http.post<ApiResponse<Follow>>(`${this.apiUrl}/follow/${userId}`, {});
  }

  /**
   * Alias for followUser to maintain compatibility with profile-view component
   * @param userId The ID of the user to follow
   */
  follow(userId: number): Observable<ApiResponse<Follow>> {
    return this.followUser(userId);
  }

  /**
   * Unfollow a user - matches your controller's DELETE /follow/{userId}
   * @param userId The ID of the user to unfollow
   */
  unfollowUser(userId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/follow/${userId}`);
  }

  /**
   * Alias for unfollowUser to maintain compatibility with profile-view component
   * @param userId The ID of the user to unfollow
   */
  unfollow(userId: number): Observable<ApiResponse<void>> {
    return this.unfollowUser(userId);
  }

  /**
   * Get followers of a user - matches your controller's GET /followers/{userId}
   * @param userId The ID of the user to get followers for
   */
  getFollowers(userId: number): Observable<ApiResponse<Follow[]>> {
    return this.http.get<ApiResponse<Follow[]>>(`${this.apiUrl}/followers/${userId}`);
  }

  /**
   * Get users that a user is following - matches your controller's GET /following/{userId}
   * @param userId The ID of the user to get following list for
   */
  getFollowing(userId: number): Observable<ApiResponse<Follow[]>> {
    return this.http.get<ApiResponse<Follow[]>>(`${this.apiUrl}/following/${userId}`);
  }

  /**
   * Check if the current user is following another user
   * @param userId The ID of the user to check
   */
  isFollowing(userId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/is-following/${userId}`);
  }

  /**
   * Get follower count for a user
   * @param userId The ID of the user
   */
  getFollowerCount(userId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/follower-count/${userId}`);
  }

  /**
   * Get following count for a user
   * @param userId The ID of the user
   */
  getFollowingCount(userId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/following-count/${userId}`);
  }

  // ========== Discovery Methods ==========

  /**
   * Get suggested connections for the current user
   * @param limit Maximum number of suggestions to return
   */
  getSuggestedConnections(limit: number = 10): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/suggestions?limit=${limit}`);
  }

  /**
   * Get mutual connections between current user and another user
   * @param userId The ID of the other user
   */
  getMutualConnections(userId: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/mutual-connections/${userId}`);
  }

  /**
   * Search for users to connect with
   * @param query Search query
   * @param page Page number
   * @param size Page size
   */
  searchUsers(query: string, page: number = 0, size: number = 20): Observable<PageResponse<User>> {
    return this.http.get<PageResponse<User>>(`${this.apiUrl}/search?q=${query}&page=${page}&size=${size}`);
  }

  /**
   * Get people you may know based on mutual connections
   * @param limit Maximum number of suggestions
   */
  getPeopleYouMayKnow(limit: number = 10): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/people-you-may-know?limit=${limit}`);
  }
}